package com.example.dru

import com.agorapulse.dru.Dru
import com.example.Query
import com.example.Temporality
import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import spock.lang.Specification

@Rollback
@Integration
class DruIntSpec extends Specification {
    Dru dru = Dru.create {
        include CommonDataSets.query
        include CommonDataSets.temporality
    }


    void setup() {
        Query.withSession {
            dru.load()
       }
    }


    def "It loads fixtures from json in an integration test1"() {
        expect:
        dru.findAllByType(Query).size() ==2
        dru.findAllByType(Temporality).size() ==2

        and:
        Query.list().size() ==2
        Temporality.list().size() ==2
    }

    def "It loads fixtures from json in an integration test2"() {
        expect:
        dru.findAllByType(Query).size() ==2
        dru.findAllByType(Temporality).size() ==2

        and:
        Query.list().size() ==2
        Temporality.list().size() ==2
    }
}
