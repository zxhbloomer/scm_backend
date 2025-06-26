package com.xinyirun.scm.quartz.config.listener;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SystemSchedulerListener implements SchedulerListener {

    @Override
    public void jobAdded(JobDetail arg0) {
        log.debug("SchedulerListener监听器：MySchedulerListener.jobAdded()");
    }

    @Override
    public void jobDeleted(JobKey arg0) {
        log.debug("SchedulerListener监听器：MySchedulerListener.jobDeleted()");
    }

    @Override
    public void jobPaused(JobKey arg0) {
        log.debug("SchedulerListener监听器：MySchedulerListener.jobPaused()");
    }

    @Override
    public void jobResumed(JobKey arg0) {
        log.debug("SchedulerListener监听器：MySchedulerListener.jobResumed()");
    }

    @Override
    public void jobScheduled(Trigger arg0) {
        log.debug("SchedulerListener监听器：MySchedulerListener.jobScheduled()");
    }

    @Override
    public void jobUnscheduled(TriggerKey arg0) {
        log.debug("SchedulerListener监听器：MySchedulerListener.jobUnscheduled()");
    }

    @Override
    public void jobsPaused(String arg0) {
        log.debug("SchedulerListener监听器：MySchedulerListener.jobsPaused()");
    }

    @Override
    public void jobsResumed(String arg0) {
        log.debug("SchedulerListener监听器：MySchedulerListener.jobsResumed()");
    }

    @Override
    public void schedulerError(String arg0, SchedulerException arg1) {
        log.debug("SchedulerListener监听器：MySchedulerListener.schedulerError()");
    }

    @Override
    public void schedulerInStandbyMode() {
        log.debug("SchedulerListener监听器：MySchedulerListener.schedulerInStandbyMode()");
    }

    @Override
    public void schedulerShutdown() {
        log.debug("SchedulerListener监听器：MySchedulerListener.schedulerShutdown()");
    }

    @Override
    public void schedulerShuttingdown() {
        log.debug("SchedulerListener监听器：MySchedulerListener.schedulerShuttingdown()");
    }

    @Override
    public void schedulerStarted() {
        log.debug("SchedulerListener监听器：MySchedulerListener.schedulerStarted()");
    }

    @Override
    public void schedulerStarting() {
        log.debug("SchedulerListener监听器：MySchedulerListener.schedulerStarting()");
    }

    @Override
    public void schedulingDataCleared() {
        log.debug("SchedulerListener监听器：MySchedulerListener.schedulingDataCleared()");
    }

    @Override
    public void triggerFinalized(Trigger arg0) {
        log.debug("SchedulerListener监听器：MySchedulerListener.triggerFinalized()");
    }

    @Override
    public void triggerPaused(TriggerKey arg0) {
        log.debug("SchedulerListener监听器：MySchedulerListener.triggerPaused()");
    }



    @Override
    public void triggersPaused(String arg0) {
        log.debug("SchedulerListener监听器：MySchedulerListener.triggersPaused()");
    }

    @Override
    public void triggersResumed(String arg0) {
        log.debug("SchedulerListener监听器：MySchedulerListener.triggersResumed()");
    }

    @Override
    public void triggerResumed(TriggerKey arg0) {
        log.debug("SchedulerListener监听器：MySchedulerListener.triggerResumed()");
    }

}