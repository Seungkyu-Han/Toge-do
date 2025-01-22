package vp.togedo.redis.config

import com.fasterxml.jackson.databind.module.SimpleModule
import org.bson.types.ObjectId

class ObjectIdModule : SimpleModule() {
    init {
        addSerializer(ObjectId::class.java, ObjectIdSerializer())
        addDeserializer(ObjectId::class.java, ObjectIdDeserializer())
    }
}