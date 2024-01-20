package util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import mu.KotlinLogging
import org.apache.avro.Schema
import org.apache.avro.generic.GenericDatumReader
import org.apache.avro.generic.GenericRecord
import org.apache.avro.io.DatumReader
import org.apache.avro.io.DatumWriter
import org.apache.avro.io.Decoder
import org.apache.avro.io.Encoder
import org.apache.avro.specific.SpecificDatumWriter
import org.apache.avro.specific.SpecificRecordBase
import java.io.ByteArrayOutputStream

object SerializationUtil {
    private val logger = KotlinLogging.logger { }

    val mapper: ObjectMapper = jacksonObjectMapper().registerModule(JavaTimeModule())

    // urls for avro schemas
    private val packageNames: List<String> = listOf(
    )

    // event name to avro schema name mapping
    private val gatewayMappings = mapOf(
    )

    fun serialise(message: GenericRecord, eventName: String): ByteArray {
        val writer: DatumWriter<GenericRecord> = SpecificDatumWriter(getSchema(eventName, packageNames))
        val out = ByteArrayOutputStream()
        val encoder: Encoder = org.apache.avro.io.EncoderFactory.get().binaryEncoder(out, null)
        writer.write(message, encoder)
        encoder.flush()
        out.close()
        return out.toByteArray()
    }

    fun deserialise(serialisedRecord: ByteArray, eventName: String): GenericRecord {
        val reader: DatumReader<Any> = GenericDatumReader(getSchema(eventName, packageNames))
        val decoder: Decoder = org.apache.avro.io.DecoderFactory.get().binaryDecoder(
            serialisedRecord, null
        )
        return reader.read(null, decoder) as GenericRecord
    }

    fun SpecificRecordBase.toString(eventName: String): String {
        val schema = getSchema(eventName, packageNames)
        val writer: DatumWriter<Any> = SpecificDatumWriter(schema)
        val out = ByteArrayOutputStream()
        val encoder: Encoder = org.apache.avro.io.EncoderFactory.get().jsonEncoder(schema, out)
        writer.write(this, encoder)
        encoder.flush()
        out.close()
        return out.toString("UTF-8")
    }

    fun String.toGenericRecord(eventName: String): GenericRecord {
        val schema = getSchema(eventName, packageNames)
        val reader: DatumReader<Any> = GenericDatumReader(schema)
        val decoder: Decoder = org.apache.avro.io.DecoderFactory.get().jsonDecoder(schema, this)
        return reader.read(null, decoder) as GenericRecord
    }

    private fun getSchema(eventType: String, packageNames: List<String>): Schema {
        logger.debug { "Serialising the message received for $eventType" }
        val classShortName = getFullyQualifiedClassName(
            gatewayMappings.getOrDefault(eventType, eventType),
            packageNames
        ) ?: throw RuntimeException("Avro class not found for $eventType in packages $packageNames")
        @Suppress("UNCHECKED_CAST") val target: Class<out SpecificRecordBase> = Class.forName(classShortName) as Class<out SpecificRecordBase>
        return target.getDeclaredConstructor().newInstance().schema
    }

    private fun getFullyQualifiedClassName(eventType: String, packageNames: List<String>): String? =
        packageNames.firstOrNull { isClassPresent("$it.$eventType") }?.plus(".$eventType")

    private fun isClassPresent(className: String?) =
        try {
            Class.forName(className)
            true
        } catch (e: ClassNotFoundException) {
            false
        }
}
