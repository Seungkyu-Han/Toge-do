package vp.togedo.redis.config

import org.bson.types.ObjectId

class ObjectIdDeserializer : com.fasterxml.jackson.databind.JsonDeserializer<ObjectId>() {
    override fun deserialize(p: com.fasterxml.jackson.core.JsonParser, ctxt: com.fasterxml.jackson.databind.DeserializationContext): ObjectId {
        return ObjectId(p.valueAsString)
    }
}