package org.ekstep.dp.domain

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import redis.clients.jedis.Jedis
import redis.clients.jedis.exceptions.JedisException
import java.util._

import org.ekstep.dp.cache.RedisConnect


class DataCache(var fieldsList: List[String]) {
  protected var redisConnect: RedisConnect = null
  protected var databaseIndex = 0
  protected var redisConnection: Jedis = null
  private val gson = new Gson

  def getData(key: String): Map[String, AnyRef] = {
    var cacheDataMap = null
    try cacheDataMap = getDataFromCache(key)
    catch {
      case ex: JedisException =>
        redisConnect.resetConnection()
        try {
          val redisConn = redisConnect.getConnection(databaseIndex)
          try {
            this.redisConnection = redisConn
            cacheDataMap = getDataFromCache(key)
          } finally if (redisConn != null) redisConn.close()
        }
    }
    if (cacheDataMap != null && !cacheDataMap.isEmpty) metrics.incCacheHitCounter()
    cacheDataMap
  }

  private def getDataFromCache(key: String) = {
    val cacheData = Map[String, AnyRef]
    val dataNode = redisConnection.get(key)
    if (dataNode != null && !dataNode.isEmpty) {
      val `type` = new TypeToken[util.Map[String, AnyRef]]() {}.getType
      val parsedData = gson.fromJson(dataNode, `type`)
      parsedData.keySet.retainAll(fieldsList)
      parsedData.values.removeAll(Collections.singleton(""))
      import scala.collection.JavaConversions._
      for (entry <- parsedData.entrySet) {
        cacheData.put(entry.getKey.toLowerCase.replace("_", ""), entry.getValue)
      }
    }
    cacheData
  }

  def getData(keys: util.List[String]): util.List[util.Map[_, _]] = {
    val list = new util.ArrayList[util.Map[_, _]]
    import scala.collection.JavaConversions._
    for (entry <- keys) {
      val data = getData(entry)
      if (data != null && !data.isEmpty) list.add(data)
    }
    list
  }
}

