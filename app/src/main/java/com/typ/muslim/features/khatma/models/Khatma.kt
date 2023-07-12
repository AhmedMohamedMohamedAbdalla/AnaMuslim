package com.typ.muslim.features.khatma.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.typ.muslim.enums.FormatPattern
import com.typ.muslim.features.khatma.utils.KhatmaConverters
import com.typ.muslim.features.quran.Quran
import com.typ.muslim.models.Timestamp
import java.io.Serializable
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Constructor used for constructing a khatma model loaded from database
 */
@Entity
@TypeConverters(KhatmaConverters::class)
class Khatma(
    @PrimaryKey(false)
    val id: String,

    var name: String? = null,

    @ColumnInfo(name = "duration")
    val plan: KhatmaPlan,

    @ColumnInfo(name = "startedIn")
    val createdIn: Long = Timestamp.NOW().toMillis(),

    @ColumnInfo()
    var reminder: ReminderPlan?,

    @ColumnInfo()
    var progress: Int = 0,

    @ColumnInfo(name = "step")
    val werdLength: Int = Quran.QURAN_PARTS_COUNT / plan.duration
) : Serializable {

    var completedParts: Int
        get() = (progress * werdLength).coerceAtMost(Quran.QURAN_PARTS_COUNT)
        set(value) {
            progress = value
        }

    val startedIn: Timestamp
        get() = Timestamp(createdIn)

    val expectedEnd: Timestamp
        get() = startedIn.clone().roll(Calendar.DATE, plan.duration)

    /**
     * Today number on this khatma
     */
    val todayNumber: Int
        get() {
            val now = Timestamp.NOW()
            if (now.isAfter(expectedEnd)) return -1
            // Calculate today
            return plan.duration - TimeUnit.MILLISECONDS.toDays(expectedEnd.toMillis() - now.toMillis()).toInt() + 1
        }

    /**
     * The current werd for this khatma
     */
    val currentWerd: KhatmaWerd
        get() {
            return KhatmaWerd(
                Quran.getPart(completedParts + 1).start,
                Quran.getPart(completedParts + werdLength).end
            )
        }

    /**
     * The next werd for this khatma (if available)
     */
    val nextWerd: KhatmaWerd?
        get() {
            return KhatmaWerd(
                Quran.getPart((completedParts + werdLength) + 1).start,
                Quran.getPart((completedParts + werdLength) + werdLength).end
            ).takeIf { hasRemainingWerds }
        }

    /**
     * `true` if khatma is still active, `false` otherwise
     */
    val isActive: Boolean
        get() = expectedEnd.isAfter(Timestamp.NOW()) and (completedParts < Quran.QURAN_PARTS_COUNT)

    /**
     * Completed werds as a percentage
     */
    val progressPercentage: Float
        get() = (completedParts / Quran.QURAN_PARTS_COUNT.toFloat() * 100f).coerceAtMost(100f) // <= 100

    /**
     * `true` if there are remaining werds, `false` otherwise
     */
    val hasRemainingWerds: Boolean
        get() = remainingWerds > 0

    /**
     * Number of werds remaining
     */
    val remainingWerds: Int
        get() = (Quran.QURAN_PARTS_COUNT - completedParts).coerceAtLeast(0)

    /**
     * List that holds records of user progress in this khatma
     */
    @Ignore
    val history = mutableListOf<KhatmaHistoryRecord>()

    /**
     * List that holds the achievements unlocked by this user
     */
    @Ignore
    val achievements = mutableListOf<KhatmaAchievement>()

    /**
     * Returns `true` if khatma has a reminder set, `false` otherwise
     */
    fun hasReminder() = reminder != null

    /**
     * Add record to the history list
     */
    fun addHistoryRecord(record: KhatmaHistoryRecord) = history.add(record)

    /**
     * Add achievement to achievements list
     */
    fun addAchievement(achievement: KhatmaAchievement) = achievements.add(achievement)

    /**
     * Mark the current werd as done, calculate next werd then refresh Khatma runtime
     */
    fun saveProgress() {
        if (hasRemainingWerds) progress++
    }

    /**
     * Changes the plan of this khatma to a new one.
     * [NOTE!!!] This method is synchronized and will be locked if called several times immediately to prevent heavy UI updating load.
     *
     * @param newPlan The new plan.
     */
    fun changePlan(newPlan: KhatmaPlan): Boolean {
        TODO("[WILL BE IMPLEMENTED IN FUTURE DEVELOPMENT].")
    }

    override fun toString(): String {
        return """
            Khatma={
                id=${id},
                name=${name},
                plan=${plan},
                createdIn=${createdIn.asTimestamp().getFormatted(FormatPattern.DATE_MONTH)},
                reminder=${reminder},
                completedWerds=${completedParts},
                werdLength=${werdLength},
                expectedEnd=${expectedEnd},
                currentWerd=${currentWerd},
                isActive=${isActive},
                todayNumber=${todayNumber},
                progressPercentage=${progressPercentage},
            }
        """.trimIndent()
    }

    fun Long.asTimestamp() = Timestamp(this)

}