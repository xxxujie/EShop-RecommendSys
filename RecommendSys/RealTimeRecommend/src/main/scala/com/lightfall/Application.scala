package com.lightfall

import java.util

import com.alibaba.fastjson.{JSON, JSONObject}
import org.apache.hadoop.fs.Path
import org.apache.hadoop.hbase.{HBaseConfiguration, TableName}
import org.apache.hadoop.hbase.client.{ConnectionFactory, Put, Table}
import org.apache.hadoop.hbase.util.Bytes
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, ConsumerStrategy, KafkaUtils, LocationStrategies, LocationStrategy}
import org.apache.spark.streaming.{Seconds, StreamingContext}

object Application {

    // hbase行号计数器
    var rcount: Long = 1


    def main(args: Array[String]): Unit = {

        val sparkConf = new SparkConf().setMaster("local[*]").setAppName("LogDatat2HBase")

        // 每 60s 一个批次
        val ssc = new StreamingContext(sparkConf, Seconds(10))

        ssc.sparkContext.setLogLevel("WARN")

        val brokers = "lightfall-df:9092"
        val topic = "mysink"
        val group = "sparkGroup"
        val kafkaParam = Map(
            "bootstrap.servers" -> brokers,
            "key.deserializer" -> classOf[StringDeserializer],
            "value.deserializer" -> classOf[StringDeserializer],
            "group.id" -> group,
            "auto.offset.reset" -> "latest",
            "enable.auto.commit" -> (true: java.lang.Boolean)
        )

        val locationStrategy: LocationStrategy = LocationStrategies.PreferConsistent
        val consumerStrategy: ConsumerStrategy[String, String] = ConsumerStrategies.Subscribe(Array(topic), kafkaParam)
        val resultDStream: InputDStream[ConsumerRecord[String, String]] = KafkaUtils.createDirectStream(ssc, locationStrategy, consumerStrategy)

        //匹配json字符串的正则表达式
        val pattern = "\\{.*\\}".r

        val map = new util.HashMap[String, String]()
        resultDStream.foreachRDD(iter => {
            if (iter.count() > 0) iter.foreach(record => {
                val str: String = record.value()
                //record.offset()
                //          println(str)
                //将json字符串里的字段解析出来
                val jsonStr: Option[String] = pattern.findFirstIn(str)
                val jsonObject = JSON.parseObject(jsonStr.get)
                val username: String = jsonObject.get("username").asInstanceOf[String]
                val userAct: String = jsonObject.get("type").asInstanceOf[String]
                val data: JSONObject = jsonObject.getJSONObject("data")
                val author: String = data.get("author").asInstanceOf[String]
                val bookTitle: String = data.get("bookName").asInstanceOf[String]
                val bookId: Int = data.get("bookId").asInstanceOf[Int]
                val price: String = data.get("price").asInstanceOf[String]
                val category: String = data.get("category").asInstanceOf[String]
                map.put("username", username)
                map.put("userAct", userAct)
                map.put("author", author)
                map.put("bookTitle", bookTitle)
                map.put("bookId", bookId + "")
                map.put("price", price)
                map.put("category", category)
                println(map.toString)

                //hbase的连接配置
                val config = HBaseConfiguration.create
                config.addResource(new Path(ClassLoader.getSystemResource("hbase-site.xml").toURI))
                config.addResource(new Path(ClassLoader.getSystemResource("core-site.xml").toURI))
                try {
                    val connection = ConnectionFactory.createConnection(config)
                    //获取hbase的表名
                    val table: Table = connection.getTable(TableName.valueOf("recommend"))
                    println("开始写入hbase")
                    println(map.toString)
                    //开始hbase操作
                    //创建一个行号（用于插入数据）
                    val put = new Put(Bytes.toBytes("" + rcount))
                    //插入列簇（已定义的）：列名：时间戳：值
                    put.addColumn(Bytes.toBytes("log"), Bytes.toBytes("username"), System.currentTimeMillis(), Bytes.toBytes(map.get("username")))
                    put.addColumn(Bytes.toBytes("log"), Bytes.toBytes("userAct"), System.currentTimeMillis(), Bytes.toBytes(map.get("userAct")))
                    put.addColumn(Bytes.toBytes("log"), Bytes.toBytes("bookTitle"), System.currentTimeMillis(), Bytes.toBytes(map.get("bookTitle")))
                    put.addColumn(Bytes.toBytes("log"), Bytes.toBytes("bookId"), System.currentTimeMillis(), Bytes.toBytes(map.get("bookId")))
                    put.addColumn(Bytes.toBytes("log"), Bytes.toBytes("author"), System.currentTimeMillis(), Bytes.toBytes(map.get("author")))
                    put.addColumn(Bytes.toBytes("log"), Bytes.toBytes("category"), System.currentTimeMillis(), Bytes.toBytes(map.get("category")))
                    put.addColumn(Bytes.toBytes("log"), Bytes.toBytes("price"), System.currentTimeMillis(), Bytes.toBytes(map.get("price")))
                    table.put(put)
                } catch {
                    case e: Exception => e.printStackTrace() //println("暂无数据")
                }
                rcount = rcount + 1
            })
        })

        ssc.start()
        ssc.awaitTermination()

    }
}
