package com.example

class Temporality {

    static final String replaceValue = ":TEMPORALITY"

    String a
    String b

    Date dateCreated
    Date lastUpdated

    static constraints = {
        a nullable:false, blank:false, maxSize: 30
        b nullable:false, blank:false, maxSize: 3
    }

    static mapping = {
        id generator: 'sequence', column: 'tempoId', params: [sequence: 'S_MAP_TEMPO']
    }

    String applyMapping(String baseQuery){
        baseQuery.replaceAll(Temporality.replaceValue, a)
    }

    String toString(){
        "==Temporality\n" +
                "a:"+a+"\n"+
                "b:"+b+"\n"
    }
}
