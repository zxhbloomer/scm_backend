---
applyTo: '**'
---

### 审批流的设定
- 是bpm不是bmp
- 审批流启用
  1、必须在servcie层，extends
  IBpmCommonCallBackService<xxxx>,          --正向审批
  IBpmCancelCommonCallBackService<xxxx>     --作废审批
  
  2、在servcieimpl层，实现方法
    正向审批

    ```
        /**
     * BPM回调-创建流程时更新bpm实例汇总数据
     */
    UpdateResultAo<Integer> bpmCallBackCreateBpm(T searchCondition);

    /**
     * BPM回调-审批通过
     */
    UpdateResultAo<Integer> bpmCallBackApprove(T searchCondition);

    /**
     * BPM回调-审批拒绝
     */
    UpdateResultAo<Integer> bpmCallBackRefuse(T searchCondition);

    /**
     * BPM回调-审批取消
     */
    UpdateResultAo<Integer> bpmCallBackCancel(T searchCondition);

    /**
     * BPM回调-保存最新审批人
     */
    UpdateResultAo<Integer> bpmCallBackSave(T searchCondition);
    ```

    作废审批
    ```
    /**
     * BPM作废回调-创建流程时更新bpm实例汇总数据
     */
    UpdateResultAo<Integer> bpmCancelCallBackCreateBpm(T searchCondition);

    /**
     * BPM作废回调-审批通过
     */
    UpdateResultAo<Integer> bpmCancelCallBackApprove(T searchCondition);
    /**
     * BPM作废回调-审批拒绝
     */
    UpdateResultAo<Integer> bpmCancelCallBackRefuse(T searchCondition);

    /**
     * BPM作废回调-审批取消
     */
    UpdateResultAo<Integer> bpmCancelCallBackCancel(T searchCondition);

    /**
     * BPM作废回调-保存最新审批人
     */
    UpdateResultAo<Integer> bpmCancelCallBackSave(T searchCondition);
    ```

- 审批流的实现具体可以学习参考样例代码：BPoContractServiceImpl.java


