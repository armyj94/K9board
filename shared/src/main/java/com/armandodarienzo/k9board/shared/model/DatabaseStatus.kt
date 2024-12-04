package com.armandodarienzo.k9board.shared.model

class DatabaseStatus (
    val tag: String,
    var state: Statuses = Statuses.NOT_DOWNLOADED,
    var progress: Float = 0F
) {
    companion object {
        enum class Statuses{
            DOWNLOADING,
            DOWNLOADED,
            NOT_DOWNLOADED,
            ERROR
        }
    }

}