package org.springframework.jenkins.scstappstarters.common

import groovy.transform.CompileStatic

/**
 * @author Soby Chacko
 */
@CompileStatic
class AllScstAppStarterJobs {

    //Removed cassandra and gpfdist from Darwin builds

    public static final Map<String, String> PHASE1_JOBS = ['jdbc-source':'source-apps-generator/jdbc-source-apps-generator', 'http-source':'source-apps-generator/http-source-apps-generator',
                                                           'splitter-processor':'processor-apps-generator/splitter-processor-apps-generator', 'rabbit-sink':'sink-apps-generator/rabbit-sink-apps-generator',
                                                           'time-source':'source-apps-generator/time-source-apps-generator', 'log-sink':'sink-apps-generator/log-sink-apps-generator']

//    public static final List<String> PHASE2_JOBS = ['loggregator', 'load-generator', 'mail',
//                                                    'pmml', 'tasklaunchrequest-transform', 'triggertask', 'transform', 'websocket', 'http']
//
//    public static final List<String> PHASE3_JOBS = ['syslog', 'filter', 'splitter', 'jms', 'groovy-filter',
//                                                    'redis-pubsub', 'tcp', 'httpclient', 'cassandra']
//
//    public static final List<String> PHASE4_JOBS = ['twitter', 'aggregator', 'mqtt', 'rabbit', 'gemfire',
//                                                    'scriptable-transform', 'ftp', 'file', 'cdc-debezium']
//
//    public static final List<String> PHASE5_JOBS = ['aws-s3', 'sftp', 'tensorflow', 'mongodb', 'hdfs', 'jdbc', 'python', 'analytics']

    public static final List<String> PHASE1_KEYS = ['jdbc-source', 'http-source', 'splitter-processor', 'rabbit-sink', 'time-source', 'log-sink']
    public static final List<List<String>> ALL_JOBS = [PHASE1_KEYS]
//            PHASE1_JOBS + PHASE2_JOBS +
//                    PHASE3_JOBS + PHASE4_JOBS +
//                    PHASE5_JOBS

//    public static final List<List<String>> PHASES = [
//            PHASE1_JOBS, PHASE2_JOBS,
//            PHASE3_JOBS, PHASE4_JOBS,
//            PHASE5_JOBS]

    //Could be useful for the release as it reduces one phase
//    public static final List<String> PHASE1_JOBS = ['log', 'time', 'throughput', 'bridge', 'groovy-transform',
//                                                    'header-enricher', 'router', 'tasklauncher-data-flow', 'grpc', 'trigger', 'loggregator']
//
//    public static final List<String> PHASE2_JOBS = ['load-generator', 'mail', 'httpclient',
//                                                    'pmml', 'tasklaunchrequest-transform', 'triggertask', 'transform', 'websocket', 'http', 'syslog', 'filter']
//
//    public static final List<String> PHASE3_JOBS = ['jms', 'groovy-filter',
//                                                    'redis-pubsub', 'tcp',
//                                                    'cassandra', 'twitter', 'mqtt', 'aggregator', 'rabbit', 'splitter']
//
//    public static final List<String> PHASE4_JOBS = ['gemfire', 'scriptable-transform', 'ftp', 'file', 'aws-s3', 'sftp',
//                                                    'tensorflow', 'mongodb', 'hdfs', 'jdbc', 'python', 'analytics']
//
//    public static final List<String> ALL_JOBS =
//            PHASE1_JOBS + PHASE2_JOBS +
//                    PHASE3_JOBS + PHASE4_JOBS
//
//    public static final List<List<String>> PHASES = [
//            PHASE1_JOBS, PHASE2_JOBS,
//            PHASE3_JOBS, PHASE4_JOBS]

}
