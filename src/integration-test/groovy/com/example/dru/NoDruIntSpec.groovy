package com.example.dru

import com.example.Query
import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import spock.lang.Specification
import spock.lang.Stepwise

@Rollback
@Integration
@Stepwise
class NoDruIntSpec extends Specification {

    //setup runs outside of the transaction boundary managed by @Rollback
    // see https://docs.grails.org/latest/guide/testing.html#functionalTesting
    // "Though each test method transaction is rolled back, the setup() method uses a separate transaction that is not rolled back.
    // Data will persist to the database and will need to be cleaned up manually if setup() sets up data and persists them "
    void setup() {
        Query.withSession {
            new Query(queryName: "a", queryBase: "A").save(flush: true)
            new Query(queryName: "b", queryBase: "B").save(flush: true)
        }
    }

    def "It expects 2 entities in the database"() {
        expect:
        Query.list().size() == 2
    }

    def "It expects 2 additional entities in the database"() {
        expect:
        Query.list().size() == 4
    }
}
