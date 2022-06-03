package me.spartacus04.instantrestock

import org.bukkit.persistence.PersistentDataType
import org.bukkit.persistence.PersistentDataAdapterContext

class TradesDataType : PersistentDataType<ByteArray, IntArray> {
    override fun getPrimitiveType(): Class<ByteArray> {
        return ByteArray::class.java
    }

    override fun getComplexType(): Class<IntArray> {
        return IntArray::class.java
    }

    override fun toPrimitive(complex: IntArray, context: PersistentDataAdapterContext): ByteArray {
        val bytes = ByteArray(complex.size)

        for (i in complex.indices) {
            bytes[i] = complex[i].toByte()
        }

        return bytes
    }

    override fun fromPrimitive(primitive: ByteArray, context: PersistentDataAdapterContext): IntArray {
        val ints = IntArray(primitive.size)

        for (i in primitive.indices) {
            ints[i] = primitive[i].toInt()
        }

        return ints
    }
}