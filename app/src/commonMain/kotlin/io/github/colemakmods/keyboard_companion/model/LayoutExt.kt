package io.github.colemakmods.keyboard_companion.model

fun Layout.dumpAll(sb: StringBuilder, geometry: Geometry) {
    dumpLayout(sb)
    dumpScores(sb, geometry)
    dumpFingers(sb, geometry)
}

fun Layout.dumpLayout(sb: StringBuilder) {
    sb.append("Keyboard $name\n")
    for (row in LayoutMapping.FORMAT_MAIN.indices) {
        for (col in LayoutMapping.FORMAT_MAIN[row].indices) {
            val keyId = LayoutMapping.getKeyId(LayoutMapping.FORMAT_MAIN, row, col)
            val baseLayer = if (mapping.getLayerCount() > 1) 1 else 0
            val ch = mapping.getLayer(baseLayer).getLabel(keyId)
            if (ch != null) sb.append(ch) else sb.append('□')
            sb.append(' ')
        }
        sb.append('\n')
    }
}

fun Layout.dumpScores(sb: StringBuilder, geometry: Geometry) {
    sb.append("\nEffort:\n")
    for (row in LayoutMapping.FORMAT_MAIN.indices) {
        for (col in LayoutMapping.FORMAT_MAIN[row].indices) {
            val keyId = LayoutMapping.getKeyId(LayoutMapping.FORMAT_MAIN, row, col)
            val key = geometry.getKey(keyId)
            sb.append(key?.score?.formatToOneDecimalPlace() ?: "□.□")
            sb.append(' ')
        }
        sb.append('\n')
    }
}

fun Layout.dumpFingers(sb: StringBuilder, geometry: Geometry) {
    sb.append("\nFingers:\n")
    for (row in LayoutMapping.FORMAT_MAIN.indices) {
        for (col in LayoutMapping.FORMAT_MAIN[row].indices) {
            val keyId = LayoutMapping.getKeyId(LayoutMapping.FORMAT_MAIN, row, col)
            val key = geometry.getKey(keyId)
            sb.append(key?.finger ?: "□")
            sb.append(' ')
        }
        sb.append('\n')
    }
}
