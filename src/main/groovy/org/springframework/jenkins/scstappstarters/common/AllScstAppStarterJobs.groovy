package org.springframework.jenkins.scstappstarters.common

import groovy.transform.CompileStatic

/**
 * @author Soby Chacko
 */
@CompileStatic
class AllScstAppStarterJobs {

    public static final Map<String, String> PHASE1_JOBS = ['jdbc-source':'source/jdbc-source', 'http-source':'source/http-source',
                                                           'splitter-processor':'processor/splitter-processor', 'rabbit-sink':'sink/rabbit-sink',
                                                           'time-source':'source/time-source', 'log-sink':'sink/log-sink',
                                                           'cassandra-sink':'sink/cassandra-sink', 'mongodb-sink':'sink/mongodb-sink',
                                                           'mongodb-source':'source/mongodb-source']

    public static final List<String> PHASE1_KEYS = ['jdbc-source', 'http-source', 'splitter-processor', 'rabbit-sink', 'time-source', 'log-sink', 'cassandra-sink', 'mongodb-sink', 'mongodb-source']

    public static final List<List<String>> ALL_JOBS = [PHASE1_KEYS]

}
