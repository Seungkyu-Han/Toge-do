package vp.togedo.redis.config

import org.bson.types.ObjectId

class ObjectIdSerializer : com.fasterxml.jackson.databind.JsonSerializer<ObjectId>() {
    override fun serialize(value: ObjectId, gen: com.fasterxml.jackson.core.JsonGenerator, serializers: com.fasterxml.jackson.databind.SerializerProvider) {
        gen.writeString(value.toHexString())
    }
}