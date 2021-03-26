package com.example.dru

import com.agorapulse.dru.Dru
import com.agorapulse.dru.PreparedDataSet
import com.example.Query
import com.example.Temporality

class CommonDataSets {
    public static final PreparedDataSet queryMapping = Dru.prepare {
        any(Query) {
            defaults {}
        }
    }

    public static final PreparedDataSet query = Dru.prepare {
        include queryMapping
        from('Query.json') {
            map {
                to(Query)
            }
        }
    }
    public static final PreparedDataSet temporalityMapping = Dru.prepare {
        any(Temporality) {
            defaults {}
        }
    }

    public static final PreparedDataSet temporality = Dru.prepare {
        include temporalityMapping
        from('Temporality.json') {
            map {
                to(Temporality)
            }
        }
    }
}
