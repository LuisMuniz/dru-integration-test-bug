# dru-integration-test-bug

## Issue
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

## Resolution

This is not an issue due to DRU.

As mentioned in the [grails documentation](https://docs.grails.org/latest/guide/testing.html#functionalTesting), `setup()` runs outside of the transaction boundary managed by `@Rollback`: 

>  Though each test method transaction is rolled back, the setup() method uses a separate transaction that is not rolled back.
>  Data will persist to the database and will need to be cleaned up manually if setup() sets up data and persists them [...]

## Conclusions, and Bonus issues

* Never set up DB in a `setup()` method, as its effects are additive and then you will have to clean in the `cleanup()` method.
* Use `withSession()` and not `withNewSession()` as hinted at in the DRU documentation. (Its code also sets up data in `setup()`)
* You need to call `flush()` on the session in order for the data to be loadable as GORM domains.  

Working code:
```groovy
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
```