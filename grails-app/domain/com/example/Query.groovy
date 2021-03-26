package com.example

class Query {
    String queryName
    String queryBase

    Date dateCreated
    Date lastUpdated

    static constraints = {
//        queryName nullable:false, blank:false, unique:true, maxSize: 100
        queryBase nullable:false, blank:false, maxSize: 500
    }

    static mapping = {
        id generator: 'sequence', column: 'queryId', params: [sequence: 'S_MAP_QUERY']
    }

    String toString(){
        "==QueryMapping\n" +
                "queryName:"+queryName+"\n"+
                "queryBase:"+queryBase+"\n"
    }
}
