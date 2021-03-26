# dru-integration-test-bug
A grails 4.0.6 application that does not roll back data loaded by DRU in integration test

Two identical tests run in an integration test marked @Rollback.
Dru is loaded on `setup()`

The second test fails because data has been duplicated. (setup has run twice and was not rolled back).

```groovy
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

```



```
Query.list().size() ==2
|     |      |      |
|     |      4      false
|     [==QueryMapping
|     queryName:foo-summary
|     queryBase:SELECT * from foo
|     , ==QueryMapping
|     queryName:bar-summary
|     queryBase:SELECT * from bar
|     , ==QueryMapping
|     queryName:foo-summary
|     queryBase:SELECT * from foo
|     , ==QueryMapping
|     queryName:bar-summary
|     queryBase:SELECT * from bar
|     ]
class com.example.Query

Condition not satisfied:

```

