package me.spartacus04.instantrestock

import org.bukkit.persistence.PersistentDataType
import org.bukkit.persistence.PersistentDataAdapterContext

/**
 * Custom PersistentDataType implementation for converting between ByteArray and IntArray.
 */
class TradesDataType : PersistentDataType<ByteArray, IntArray> {
    /**
     * Returns the primitive type of the data.
     *
     * @return The primitive type, which is ByteArray in this case.
     */
    override fun getPrimitiveType(): Class<ByteArray> {
        return ByteArray::class.java
    }

    /**
     * Returns the complex type of the data.
     *
     * @return The complex type, which is IntArray in this case.
     */
    override fun getComplexType(): Class<IntArray> {
        return IntArray::class.java
    }

    /**
     * Converts the complex type (IntArray) to the primitive type (ByteArray).
     *
     * @param complex The complex type to convert.
     * @param context The PersistentDataAdapterContext.
     * @return The converted primitive type (ByteArray).
     */
    override fun toPrimitive(complex: IntArray, context: PersistentDataAdapterContext): ByteArray {
        val bytes = ByteArray(complex.size)

        for (i in complex.indices) {
            bytes[i] = complex[i].toByte()
        }

        return bytes
    }

    /**
     * Converts the primitive type (ByteArray) to the complex type (IntArray).
     *
     * @param primitive The primitive type to convert.
     * @param context The PersistentDataAdapterContext.
     * @return The converted complex type (IntArray).
     */
    override fun fromPrimitive(primitive: ByteArray, context: PersistentDataAdapterContext): IntArray {
        val ints = IntArray(primitive.size)

        for (i in primitive.indices) {
            ints[i] = primitive[i].toInt()
        }

        return ints
    }
}