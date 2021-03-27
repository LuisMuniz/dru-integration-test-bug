package com.example.dru

import com.agorapulse.dru.Dru
import com.example.Query
import com.example.Temporality
import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import spock.lang.AutoCleanup
import spock.lang.Specification

@Rollback
@Integration
class DruIntSpec extends Specification {
    @AutoCleanup
    Dru dru = Dru.create {
        include CommonDataSets.query
        include CommonDataSets.temporality
    }


/*
    //setup runs outside of the transaction boundary managed by @Rollback
    // see https://docs.grails.org/latest/guide/testing.html#functionalTesting
    // "Though each test method transaction is rolled back, the setup() method uses a separate transaction that is not rolled back.
    // Data will persist to the database and will need to be cleaned up manually if setup() sets up data and persists them "
    void setup() {
        druLoad()
    }
*/


    def "It loads data with DRU in an integration test"() {
        given:
        druLoad()

        expect:
        dru.findAllByType(Query).size() == 2
        dru.findAllByType(Temporality).size() == 2

        and:
        Query.list().size() == 2
        Temporality.list().size() == 2
    }

    def "It loads the same data with DRU in another integration test"() {
        given:
        druLoad()

        expect:
        dru.findAllByType(Query).size() == 2
        dru.findAllByType(Temporality).size() == 2

        and:
        Query.list().size() == 2
        Temporality.list().size() == 2
    }

    /*
     * Helpers
     */
    private Object druLoad() {
        Query.withSession { s ->
            dru.load()
            //will not work without flushing the session...
            s.flush()
        }
    }
}
