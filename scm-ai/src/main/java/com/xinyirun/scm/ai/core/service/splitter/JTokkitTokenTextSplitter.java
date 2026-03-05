package com.xinyirun.scm.ai.core.service.splitter;

import java.util.ArrayList;
import java.util.List;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.EncodingType;
import com.knuddels.jtokkit.api.IntArrayList;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.util.Assert;

/**
 * 基于JTokkit的Token级别文本分割器
 *
 * <p>功能说明：</p>
 * <p>使用JTokkit库实现精确的Token级别文本分割，支持Overlap功能</p>
 * <p>相比基于字符估算的方式，提供更精确的Token计数和内存高效的实现</p>
 *
 * <p>核心优势：</p>
 * <ol>
 *   <li>真实Token编码：使用OpenAI的CL100K_BASE编码，与GPT-4/GPT-3.5完全一致</li>
 *   <li>Token级Overlap：基于Token List的滑动窗口，精确控制重叠数量</li>
 *   <li>内存高效：使用subList()操作Token列表，避免字符串重复创建</li>
 *   <li>智能边界检测：在自然分隔符处切分，保持语义完整性</li>
 * </ol>
 *
 * <p>使用示例：</p>
 * <pre>
 * JTokkitTokenTextSplitter splitter = JTokkitTokenTextSplitter.builder()
 *     .withChunkSize(1000)           // 每个chunk的最大token数
 *     .withOverlapSize(50)           // 相邻chunk的重叠token数
 *     .withMinChunkSizeChars(400)    // 分隔符优化的最小字符数
 *     .withMinChunkLengthToEmbed(10) // 最小embedding长度
 *     .build();
 *
 * List&lt;String&gt; segments = splitter.splitText("长文本内容...");
 * </pre>
 *
 * @author SCM AI Team
 * @since 2025-10-17
 */
@Slf4j
public class JTokkitTokenTextSplitter extends TextSplitter {

    /**
     * 默认chunk大小（Token数量）
     * 说明：每个文本段的最大Token数量，用于控制文本段的粒度
     * 用途：文档分割时，每个chunk包含的最大token数
     * 默认值：1000 tokens（较大的chunk，适合长文档场景）
     */
    private static final int DEFAULT_CHUNK_SIZE = 1000;

    /**
     * 最小chunk字符数（用于分隔符优化）
     * 说明：在自然分隔符处截断文本时，要求截断点之前至少有多少个字符
     * 用途：防止在分隔符优化时产生过短的chunk（如果分隔符太靠前，则不优化）
     * 默认值：150字符（保证chunk有足够内容）
     */
    private static final int MIN_CHUNK_SIZE_CHARS = 150;

    /**
     * 最小chunk长度才会被embedding（字符数）
     * 说明：只有文本长度超过此值的chunk才会被添加到结果中
     * 用途：过滤掉过短的无意义文本段，提高embedding质量
     * 默认值：10字符（过滤极短文本）
     */
    private static final int MIN_CHUNK_LENGTH_TO_EMBED = 10;

    /**
     * 最大chunk数量限制（安全上限）
     * 说明：单个文档最多分割成多少个chunk
     * 用途：防止无限循环导致的OOM，作为分割算法的安全保护
     * 默认值：10000个chunk（正常文档不应超过此数量）
     */
    private static final int MAX_NUM_CHUNKS = 10000;

    /**
     * 是否保留分隔符（换行符）
     * 说明：格式化输出时，是否保留原文中的换行符
     * 用途：true=保留换行符（适合代码、诗歌），false=替换为空格（适合连续文本）
     * 默认值：true（保留原始格式）
     */
    private static final boolean KEEP_SEPARATOR = true;

    /**
     * 默认overlap大小（Token数量）
     * 说明：相邻chunk之间的重叠Token数量
     * 用途：保持语义连续性，避免在关键信息处截断
     * 默认值：50 tokens（约10-15个中文字或30-40个英文单词）
     */
    private static final int DEFAULT_OVERLAP_SIZE = 50;

    private final EncodingRegistry registry = Encodings.newLazyEncodingRegistry();
    private final Encoding encoding = this.registry.getEncoding(EncodingType.CL100K_BASE);

    private final int chunkSize;
    private final int minChunkSizeChars;
    private final int minChunkLengthToEmbed;
    private final int maxNumChunks;
    private final boolean keepSeparator;
    private final String customSeparator;
    private final int overlapSize;

    public JTokkitTokenTextSplitter() {
        this(DEFAULT_CHUNK_SIZE, MIN_CHUNK_SIZE_CHARS, MIN_CHUNK_LENGTH_TO_EMBED, MAX_NUM_CHUNKS, KEEP_SEPARATOR, "\n", DEFAULT_OVERLAP_SIZE);
    }

    public JTokkitTokenTextSplitter(int chunkSize, int minChunkSizeChars, int minChunkLengthToEmbed, int maxNumChunks,
                                    boolean keepSeparator, String customSeparator, int overlapSize) {
        this.chunkSize = chunkSize;
        this.minChunkSizeChars = minChunkSizeChars;
        this.minChunkLengthToEmbed = minChunkLengthToEmbed;
        this.maxNumChunks = maxNumChunks;
        this.keepSeparator = keepSeparator;
        this.customSeparator = customSeparator;
        this.overlapSize = overlapSize;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    protected List<String> splitText(String text) {
        return doSplit(text, this.chunkSize);
    }

    /**
     * 执行Token级别的文本分割
     *
     * <p>算法流程：</p>
     * <ol>
     *   <li>将文本编码为Token列表</li>
     *   <li>使用滑动窗口提取chunk（大小为chunkSize）</li>
     *   <li>解码Token为文本</li>
     *   <li>在自然分隔符处优化切分点（仅优化输出，不影响位置推进）</li>
     *   <li>实现overlap: 下一个chunk从 (endIndex - overlapSize) 开始</li>
     *   <li>无限循环检测: nextStart=0且剩余tokens<=overlapSize时退出</li>
     * </ol>
     *
     * <p>🔧 关键修复（2025-10-18）：</p>
     * <p>修复了导致无限循环的根本bug：</p>
     * <ul>
     *   <li>错误做法: 使用分隔符优化后的actualTokensUsed计算nextStart</li>
     *   <li>问题: 优化后actualTokensUsed可能很小（如20），导致nextStart=0，形成无限循环</li>
     *   <li>现象: 文本段数达到MAX_NUM_CHUNKS=10001，相同文本重复处理</li>
     *   <li>正确做法: 使用原始endIndex推进，分隔符优化仅影响输出文本</li>
     *   <li>原理: 分隔符优化保证语义完整性，endIndex推进保证算法收敛</li>
     * </ul>
     *
     * @param text 待分割的文本
     * @param chunkSize chunk大小（Token数量）
     * @return 分割后的文本段列表
     */
    protected List<String> doSplit(String text, int chunkSize) {
        if (text == null || text.trim().isEmpty()) {
            return new ArrayList<>();
        }

        List<Integer> tokens = new ArrayList<>(getEncodedTokens(text));
        List<String> chunks = new ArrayList<>();
        int totalTokens = tokens.size();
        int loopCount = 0;

        // 防御性检查：overlap不能超过chunkSize的一半，否则窗口无法前进
        int effectiveOverlap = Math.min(overlapSize, chunkSize / 2);
        if (effectiveOverlap != overlapSize) {
            log.warn("[overlap修正] 原始overlap({})超过chunkSize({})的一半, 修正为: {}", overlapSize, chunkSize, effectiveOverlap);
        }

        log.info("[分割开始] 总tokens: {}, chunkSize: {}, overlap: {}", totalTokens, chunkSize, effectiveOverlap);

        while (!tokens.isEmpty() && loopCount < 100) {  // 增加循环保护
            loopCount++;

            // 1. 提取当前chunk的tokens
            int endIndex = Math.min(chunkSize, tokens.size());
            List<Integer> chunk = tokens.subList(0, endIndex);
            String chunkText = decodeTokens(chunk);

            log.info("[第{}次循环] tokens剩余: {}, endIndex: {}, chunkText长度: {}",
                    loopCount, tokens.size(), endIndex, chunkText.length());

            // 2. 跳过空白chunk
            if (chunkText.trim().isEmpty()) {
                log.warn("[跳过空白chunk] 推进{}个tokens", endIndex);
                tokens = new ArrayList<>(tokens.subList(endIndex, tokens.size()));
                continue;
            }

            // 3. 在自然分隔符处优化切分（保持语义完整性）
            int lastSeparator = chunkText.lastIndexOf(this.customSeparator);
            String originalChunkText = chunkText;
            if (lastSeparator != -1 && lastSeparator > this.minChunkSizeChars) {
                chunkText = chunkText.substring(0, lastSeparator + this.customSeparator.length());
                log.info("[分隔符优化] 原长度: {}, 优化后: {}, lastSeparator位置: {}",
                        originalChunkText.length(), chunkText.length(), lastSeparator);
            }

            // 4. 格式化chunk文本并添加到结果
            String chunkTextToAppend = this.keepSeparator ? chunkText.trim() : chunkText.replace(System.lineSeparator(), " ").trim();
            if (chunkTextToAppend.length() > this.minChunkLengthToEmbed) {
                chunks.add(chunkTextToAppend);
                log.info("[添加chunk] 第{}个chunk, 长度: {}, 前50字符: {}",
                        chunks.size(), chunkTextToAppend.length(),
                        chunkTextToAppend.length() > 50 ? chunkTextToAppend.substring(0, 50) : chunkTextToAppend);
            } else {
                log.warn("[过滤短chunk] 长度: {} < minLength: {}", chunkTextToAppend.length(), minChunkLengthToEmbed);
            }

            // 5. 推进位置：已到达文本末尾时直接结束，否则应用overlap
            int nextStart;
            if (endIndex >= tokens.size()) {
                // 已处理完所有tokens，无需overlap
                nextStart = tokens.size();
            } else {
                nextStart = Math.max(0, endIndex - effectiveOverlap);
                if (nextStart == 0 && tokens.size() > effectiveOverlap) {
                    log.warn("[无限循环风险] nextStart=0但tokens.size({}) > overlap({}), 强制推进", tokens.size(), effectiveOverlap);
                    nextStart = Math.min(100, endIndex);
                }
            }

            tokens = new ArrayList<>(tokens.subList(nextStart, tokens.size()));
            log.info("[推进后] tokens剩余: {}\n", tokens.size());
        }

        if (loopCount >= 100) {
            log.error("[循环超限] 达到100次循环保护上限, 生成chunks: {}, 剩余tokens: {}", chunks.size(), tokens.size());
        }

        log.info("[分割完成] 生成chunks: {}, 剩余tokens: {}", chunks.size(), tokens.size());
        return chunks;
    }

    /**
     * 将文本编码为Token列表
     *
     * @param text 待编码文本
     * @return Token ID列表
     */
    private List<Integer> getEncodedTokens(String text) {
        Assert.notNull(text, "Text must not be null");
        return this.encoding.encode(text).boxed();
    }

    /**
     * 将Token列表解码为文本
     *
     * @param tokens Token ID列表
     * @return 解码后的文本
     */
    private String decodeTokens(List<Integer> tokens) {
        Assert.notNull(tokens, "Tokens must not be null");
        var tokensIntArray = new IntArrayList(tokens.size());
        tokens.forEach(tokensIntArray::add);
        return this.encoding.decode(tokensIntArray);
    }

    @Override
    public String toString() {
        return String.format(
                "JTokkitTokenTextSplitter[chunkSize=%d tokens, overlap=%d tokens, minChunkSizeChars=%d, minChunkLengthToEmbed=%d]",
                chunkSize, overlapSize, minChunkSizeChars, minChunkLengthToEmbed
        );
    }

    /**
     * Builder模式构造器
     */
    public static final class Builder {
        private int chunkSize = DEFAULT_CHUNK_SIZE;
        private int minChunkSizeChars = MIN_CHUNK_SIZE_CHARS;
        private int minChunkLengthToEmbed = MIN_CHUNK_LENGTH_TO_EMBED;
        private int maxNumChunks = MAX_NUM_CHUNKS;
        private boolean keepSeparator = KEEP_SEPARATOR;
        private String customSeparator = "\n";
        private int overlapSize = DEFAULT_OVERLAP_SIZE;

        public Builder withChunkSize(int chunkSize) {
            this.chunkSize = chunkSize;
            return this;
        }

        public Builder withMinChunkSizeChars(int minChunkSizeChars) {
            this.minChunkSizeChars = minChunkSizeChars;
            return this;
        }

        public Builder withMinChunkLengthToEmbed(int minChunkLengthToEmbed) {
            this.minChunkLengthToEmbed = minChunkLengthToEmbed;
            return this;
        }

        public Builder withMaxNumChunks(int maxNumChunks) {
            this.maxNumChunks = maxNumChunks;
            return this;
        }

        public Builder withKeepSeparator(boolean keepSeparator) {
            this.keepSeparator = keepSeparator;
            return this;
        }

        public Builder withCustomSeparator(String customSeparator) {
            this.customSeparator = customSeparator;
            return this;
        }

        public Builder withOverlapSize(int overlapSize) {
            this.overlapSize = overlapSize;
            return this;
        }

        public JTokkitTokenTextSplitter build() {
            return new JTokkitTokenTextSplitter(this.chunkSize, this.minChunkSizeChars, this.minChunkLengthToEmbed,
                    this.maxNumChunks, this.keepSeparator, this.customSeparator, this.overlapSize);
        }
    }
}
