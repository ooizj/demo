package me.ooi.demo.testmongodb312;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.log4j.BasicConfigurator;
import org.bson.Document;
import org.junit.Before;
import org.junit.Test;

/**
 * @author jun.zhao
 */
public class TestMongoDB {

//    SQL术语/概念	MongoDB术语/概念	解释/说明
//    database	    database	数据库
//    table	        collection	数据库表/集合
//    row	        document	数据记录行/文档
//    column	    field	    数据字段/域
//    index	        index	    索引
//    table         joins	 	表连接,MongoDB不支持
//    primary key	primary key	主键,MongoDB自动将_id字段设置为主键

    //支持的类型
//    Type	            Number  Alias	Notes
//    Double	        1	"double"
//    32-bit integer	16	"int"
//    64-bit integer	18	"long"
//    Boolean	        8	"bool"
//    String	        2	"string"
//    Date	            9	"date"
//    Timestamp	        17	"timestamp"
//    Object	        3	"object"
//    Null	            10	"null"
//    Array	            4	"array"
//    ObjectId	        7	"objectId"
//    Binary data	5	"binData"
//    Undefined	6	"undefined"	Deprecated.
//    Regular Expression	11	"regex"
//    DBPointer	12	"dbPointer"	Deprecated.
//            JavaScript	13	"javascript"
//    Symbol	14	"symbol"	Deprecated.
//    JavaScript code with scope	15	"javascriptWithScope"	Deprecated in MongoDB 4.4.
//    Decimal128	19	"decimal"	New in version 3.4.
//    Min key	-1	"minKey"
//    Max key	127	"maxKey"

    @Before
    public void init(){
        BasicConfigurator.configure();
    }

    @Test
    public void testInsertDocument(){

        try(MongoClient mongoClient = new            MongoClient(new MongoClientURI("mongodb://localhost:27017"))) {
            MongoDatabase db = mongoClient.getDatabase("db1");
            MongoCollection<Document> collection = db.getCollection("c1", Document.class);

            //新增测试数据
            collection.insertOne(new Document()
                    .append("name", "xiaoming")
                    .append("age", 15));
        }
    }

    @Test
    public void testInsert(){
        try(MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"))) {
            MongoDatabase db = mongoClient.getDatabase("db1");
            System.out.println(db);
            MongoCollection<Document> collection = db.getCollection("c1", Document.class);
            System.out.println(collection);

            collection.insertOne(new Document()
                    .append("name", "xiaoming")
                    .append("age", 15));
        }
    }

}
