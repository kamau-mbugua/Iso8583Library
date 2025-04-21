package com.rbs.iso8583lib.utils.cores.extensions


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.jpos.iso.ISOMsg
import org.jpos.iso.packager.GenericPackager
import org.json.JSONObject
//import org.ocpsoft.prettytime.PrettyTime
import timber.log.Timber
import java.math.RoundingMode
import java.nio.charset.Charset
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.roundToInt


/**
 * Kotlin Extensions for simpler, easier and funw way
 * of launching of Activities
 */
inline fun <reified T : Any> Activity.launchActivity(
    requestCode: Int = -1, options: Bundle? = null, noinline init: Intent.() -> Unit = {}
) {
    val intent = newIntent<T>(this)
    intent.init()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        startActivityForResult(intent, requestCode, options)
        startActivityForResult(intent, requestCode, options)
    } else {
        startActivityForResult(intent, requestCode)
    }
}

inline fun <reified T : Any> Context.launchActivity(
    options: Bundle? = null, noinline init: Intent.() -> Unit = {}
) {
    val intent = newIntent<T>(this)
    intent.init()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        startActivity(intent, options)
    } else {
        startActivity(intent)
    }
}

inline fun <reified T : Any> newIntent(context: Context): Intent = Intent(context, T::class.java)


fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

//invisible
fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.invinsible() {
    this.visibility = View.INVISIBLE
}

fun displaySnackBarWithBottomMargin(snackbar: Snackbar, margin: Int, bottomMargin: Int) {
    val snackbarView = snackbar.view
    val params = snackbarView.layoutParams

    if (params is FrameLayout.LayoutParams) {
        params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, bottomMargin)
        snackbarView.layoutParams = params
    } else if (params is CoordinatorLayout.LayoutParams) {
        params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, bottomMargin)
        snackbarView.layoutParams = params
    }

    snackbar.show()
}

fun String.getLastNineDigits(): String {
    // Remove all whitespace from the string
    val noWhitespaceString = this.replace("\\s".toRegex(), "")

    // Get the last 9 characters, or all characters if the string is shorter than 9
    val startIndex = if (noWhitespaceString.length > 9) noWhitespaceString.length - 9 else 0
    return noWhitespaceString.substring(startIndex)
}

/**
 * Converts an object to its JSON representation.
 * @return the JSON string representation of the object.
 */
inline fun <reified T> T.toJson(): String {
    return Gson().toJson(this)
}

fun String.firstWordWithEllipsis(): String {
    val words = this.split(" ")
    return if (words.size > 1) {
        "${words[0]}..."
    } else {
        this
    }
}


fun String.getFirstWord(): String {
    val words = this.trim().split("\\s+".toRegex())
    return if (words.isNotEmpty()) words[0] else ""
}


inline fun <reified T> String.fromJson(): T {

    if (this.isNullOrEmpty()) {
        return Gson().fromJson("{}", T::class.java)
    }
    return Gson().fromJson(this, T::class.java)
}


fun String.toISOMsg(packager: GenericPackager): ISOMsg {
    val isoMsg = ISOMsg()
    isoMsg.packager = packager

    try {
        val isoMap = Gson().fromJson(this, Map::class.java) as Map<String, String>
        isoMap.forEach { (key, value) ->
            val fieldId = key.removePrefix("P-").toIntOrNull()
            if (fieldId != null) {
                isoMsg.set(fieldId, value)
            }
        }
    } catch (e: Exception) {
        Timber.e("Failed to parse ISOMsg from JSON: ${e.message}")
    }

    return isoMsg
}


inline fun <reified T> String?.fromJsonToMutableList(): MutableList<T> {
    if (this.isNullOrEmpty()) {
        return mutableListOf()
    }
    val gson = Gson()
    return gson.fromJson(this, object : TypeToken<MutableList<T>>() {}.type)
}
//Convert Json to list extension

inline fun <reified T> String?.fromJsonToList(): List<T> {
    if (this.isNullOrEmpty()) {
        return emptyList()
    }
    val gson = Gson()
    return gson.fromJson(this, object : TypeToken<List<T>>() {}.type)
}


//remove dublicate from a mutable list
fun <T> MutableList<T>.removeDuplicates(): MutableList<T> {
    val set = HashSet<T>()
    val newList = mutableListOf<T>()
    for (element in this) {
        if (set.add(element)) {
            newList.add(element)
        }
    }
    return newList
}

//decode token and return customer id
fun String.extractCustomerId(): String? {
    val token = split(".")[1]
    val decodedToken = String(Base64.decode(token, Base64.DEFAULT))
    return JSONObject(decodedToken).getString("customerId")
    Timber.e("TenantId ${JSONObject(decodedToken).getString("customerId")}")
}

fun String.extractBusinessNumber(): String? {
    val token = split(".")[1]
    val decodedToken = String(Base64.decode(token, Base64.DEFAULT))
    return JSONObject(decodedToken).getString("bid")
    Timber.e("bid ${JSONObject(decodedToken).getString("bid")}")
}

fun String.extractDateTime(): String? {
    val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
    return try {
        OffsetDateTime.parse(this, formatter)
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
    } catch (e: Exception) {
        null
    }
}


fun String.removeTextAfterDash(): String {
    return this.substringBefore("-").trim()
}

fun Double.convertToNextInteger(): Int {
    Timber.e("convertToNextInteger $this")
    val intValue = if (this >= this.toInt() + 0.1) {
        Timber.e("convertToNextInteger 0 ${this.toInt() + 1}")
        this.toInt() + 1
    } else {
        Timber.e("convertToNextInteger 1 ${this.toInt() + 1}")
        this.toInt()
    }

    return intValue
}


fun Double.roundUp(): Int {
    return Math.ceil(this).toInt()
}

fun String.removeComma(): Double {
    return this.replace(",", "").toDouble()
}

fun String.ensureTwoDecimalPlaces(): String {

    //Remove commas from the amount string
    if (this.contains(",")) {
        return this.replace(",", "")
    }

    return if (this.contains('.') && this.split('.')[1].length == 2) {
        this
    } else {
        "$this.00"
    }
}

//
fun Double.roundOff(): Int {
    return this.roundToInt()
}

fun Double.roundOff2Decimal(): Double {
    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.CEILING
    return df.format(this).toDouble()
}


//convert double to string and remove trailing zeros after decimal point   and "," from string if it contains

fun round(value: Double, places: Int): Double {
    var value = value
    require(places >= 0)
    val factor = Math.pow(10.0, places.toDouble()).toLong()
    value = value * factor
    val tmp = Math.round(value)
    return tmp.toDouble() / factor
}

fun String.toDate(
    dateFormat: String = "yyyy-MM-dd'T'HH:mm", timeZone: TimeZone = TimeZone.getTimeZone("UTC")
): Date {
    val parser = SimpleDateFormat(dateFormat, Locale.getDefault())
    parser.timeZone = timeZone
    return parser.parse(this)
}

fun Date.formatTo(dateFormat: String, timeZone: TimeZone = TimeZone.getDefault()): String {
    val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
    formatter.timeZone = timeZone
    return formatter.format(this)
}

fun dateConvertor(inputDateString: String): String {
    val instant = Instant.parse(inputDateString)
    val zoneId = ZoneId.systemDefault()
    val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withZone(zoneId)
    val outputDateString = formatter.format(instant)
    val parts = outputDateString.split(" ")
    val monthString = parts[0]
    val yearString = parts[2]
    println("$monthString $yearString")
    return "$monthString $yearString"
}

fun String.toDateFormat(): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    inputFormat.timeZone = TimeZone.getTimeZone("UTC")
    val date = inputFormat.parse(this)
    return outputFormat.format(date)
}

//fun String.getFormartedJoinedDate(): String? {
//    return try {
//        // 2021-07-22T00:21:16.341432
//        val parse = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
//        parse.timeZone = TimeZone.getTimeZone("UTC")
//
//        val date = parse.parse(this)
//        PrettyTime().format(date)
//    } catch (e: Exception) {
//        this
//    }
//}

fun String.addThreeHours(): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    inputFormat.timeZone = TimeZone.getTimeZone("UTC")
    val date = inputFormat.parse(this)

    val calendar = Calendar.getInstance()
    calendar.time = date
    calendar.add(Calendar.HOUR_OF_DAY, 0)

    val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    return outputFormat.format(calendar.time)
}

fun String.getTime(isShowSec: Boolean = false): String? {
    return try {
        // 2021-07-22T00:21:16.341432
        /*   val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSS", Locale.getDefault()).parse(
               startDate
           )*//* val date = SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault()).parse(
             startDate
         )*/
        //  PrettyTime().format(date)
        val inDate = this.toDate()

        //  inDate?.formatTo("HH:mm")

        if (isShowSec) {
            inDate.formatTo("HH:mm")
        } else {
            inDate.formatTo("hh:mm a")
        }

    } catch (e: Exception) {
        this
    }
}


fun String.getDate(): String? {
    return try {
        // 2021-07-22T00:21:16.341432
        /*   val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSS", Locale.getDefault()).parse(
               startDate
           )*//* val date = SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault()).parse(
             startDate
         )*/
        //  PrettyTime().format(date)
        val inDate = this.toDate()

        //  inDate?.formatTo("HH:mm")
        // inDate.formatTo("EEE, d MMM yyy")
        inDate.formatTo("MM/dd/yyy")

    } catch (e: Exception) {
        this
    }
}

fun String.getServerDate(): String {
    return try {
        // 2021-07-22T00:21:16.341432
        /*   val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSS", Locale.getDefault()).parse(
               startDate
           )*//* val date = SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault()).parse(
             startDate
         )*/
        //  PrettyTime().format(date)
        val inDate = this.toDate()

        //  inDate?.formatTo("HH:mm")
        // inDate.formatTo("EEE, d MMM yyy")

        inDate.formatTo("yyy-MM-dd")

    } catch (e: Exception) {
        this
    }
}


fun String.getTimeAgo(): String {
    val currentTime = Calendar.getInstance().timeInMillis
    val timeDifference = currentTime - this.toDate().time

    val seconds = timeDifference / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        days > 0 -> "$days days ago"
        hours > 0 -> "$hours hours ago"
        minutes > 0 -> "$minutes minutes ago"
        else -> "$seconds seconds ago"
    }
}

fun String.convertToCustomDateFormat(): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    val outputFormat = SimpleDateFormat("yyyy-MM-dd h:mm a", Locale.getDefault())
    //add 3 hrs
    val date = inputFormat.parse(this)
    return outputFormat.format(date)
}

fun String.convertToCustomDateFormatNew(): String {
    return this.getDate() + " " + this.getTime()
}


fun String.convertTimestamp(isDate: Boolean): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val outputFormat1 = SimpleDateFormat("h:mm a", Locale.getDefault())
    val date = inputFormat.parse(this)

    return if (isDate) {
        outputFormat.format(date)
    } else {
        outputFormat1.format(date)
    }
}

fun String.getUserNameFromToken(): String {
    val token = this
    val jwtBody = token.split(".")[1]
    val decodedBytes = java.util.Base64.getDecoder().decode(jwtBody)
    val decodedBody = String(decodedBytes, Charset.defaultCharset())
    val json = JSONObject(decodedBody)
    return json.optString("userName")
}

fun String.convertToCustomDateFormatMain(): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    val outputFormat = SimpleDateFormat("yyyy-MM-dd h:mm a", Locale.getDefault())
    inputFormat.timeZone = TimeZone.getTimeZone("UTC")
    val cleanedString = this.replace("\"", "")
    val date = inputFormat.parse(cleanedString)
    return outputFormat.format(date)
}

fun String.convertDateFormat(): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
    val outputFormat = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
    inputFormat.timeZone = TimeZone.getTimeZone("UTC")
    val date = inputFormat.parse(this)
    return outputFormat.format(date!!)
}


fun returnErrorMesssage(code: Int): String {
    return when (code) {
        0 -> {
            "Successfully printed"
        }

        240 -> {
            "Printer out of paper"
        }

        -2 -> {
            "Printer Battery low"
        }

        247 -> {
            "Printer is busy"
        }

        -256 -> {
            "Printer Encountered an error"
        }

        -257 -> {
            "Printer is not connected"
        }

        -3 -> {
            "Printer is not powered on"
        }

        225 -> {
            "POS Battery low, please charge the POS"
        }

        else -> {
            "Printer error occurred"
        }
    }
}

fun String.formatRegistrationNumber(): String {
    return try {
        this.substring(0, 3) + " " + this.substring(3)
    } catch (e: Exception) {
        this
    }
}

fun String.formatRegNoPayload(): String {
    return this.replace(" ", "")
}

fun String.cleanPhoneNumber(): String {
    if (!this.startsWith("+")) {
        return "+" + this
    }
    if (this.startsWith("0")) {
        return this.replaceFirst("0", "+254")
    }
    if (this.startsWith("7")) {
        return "+254" + this
    }
    if (this.startsWith("2547")) {
        return "+" + this
    }
    if (this.startsWith("25407")) {//remove 0 from 25407
        this.replaceFirst("0", "")
        return "+" + this
    }
    if (this.startsWith("+25407")) {
        //remove 0 from 25407
        this.replaceFirst("0", "")
    }
    return this

}


fun String.cleanPhoneNumberKCB(): String {
    return if (this.startsWith("0")) {
        if (this.length == 10) {
            this.substring(1)
        } else {
            this
        }

    } else {
        this
    }
}

fun String.maskPhoneNumber(): String {
    //if this does not start with + add a + to i
    try {
        if (this.contains(Regex("^[0-9]*$"))) {
            val visibleEnd = this.substring(this.length - 3)
            return "+2547*****$visibleEnd"
        } else if (this.contains("+")) {
            val visibleEnd = this.substring(this.length - 3)
            return "+2547*****$visibleEnd"
        }
        return this
    } catch (e: Exception) {
        return this
    }
}

fun String.concatinateName(): String {
    return if (this.length > 10) {
        this.substring(0, 10) + "..."
    } else {
        this
    }
}

fun getDateReport(reportDate: String): String {
    return try {
        val instant = Instant.parse(reportDate)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        println(instant.atZone(ZoneId.of("UTC")).format(formatter))
        instant.atZone(ZoneId.of("UTC")).format(formatter)
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
        reportDate
    }
}

fun convertDateToUTC(date: String?): String {
    var utcDate = ""
    try {
        val sdf = SimpleDateFormat("MM/dd/yy hh:mm aa", Locale.getDefault())
        val date1 = sdf.parse(date)
        val sdf2 = SimpleDateFormat("yyyy-MM-dd hh:mm aa", Locale.getDefault())
        utcDate = sdf2.format(date1)
        println("UTC Date: $utcDate")
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return utcDate
}

fun String.getCountryCode(): String? {
    return Locale.getISOCountries().find { Locale("", it).displayCountry == this }
}


fun checkProductName(productName: String): String {
    return if (productName.length > 16) {
        productName.substring(0, 13) + "...".uppercase(Locale.ROOT).padStart(0)
    } else if (productName.length < 16) {
        productName.padEnd(16, ' ').uppercase(Locale.ROOT).padStart(0)
    } else {
        productName.uppercase(Locale.ROOT).padStart(0)
    }
}

fun String.checkProductNameExt(): String {
    return if (this.length > 18) {
        this.uppercase(Locale.getDefault()).substring(0, 15) + "...".uppercase(Locale.ROOT).padStart(0)
    } else if (this.length < 18) {
        this.uppercase(Locale.getDefault()).padEnd(18, ' ').uppercase(Locale.ROOT).padStart(0)
    } else {
        this.uppercase(Locale.ROOT).padStart(0)
    }
}

fun String.checkProductNameExtTransport(): String {
    return if (this.length > 29) {
        this.uppercase(Locale.getDefault()).substring(0, 26) + "...".uppercase(Locale.ROOT).padStart(0)
    } else if (this.length < 29) {
        this.uppercase(Locale.getDefault()).padEnd(29, ' ').uppercase(Locale.ROOT).padStart(0)
    } else {
        this.uppercase(Locale.ROOT).padStart(0)
    }
}

fun String.checkProductNameExtInv(): String {
    return if (this.length > 25) {
        this.uppercase(Locale.getDefault()).substring(0, 22) + "...".uppercase(Locale.ROOT).padStart(0)
    } else if (this.length < 25) {
        this.uppercase(Locale.getDefault()).padEnd(25, ' ').uppercase(Locale.ROOT).padStart(0)
    } else {
        this.uppercase(Locale.ROOT).padStart(0)
    }
}

fun String.checkTripTransport(): String {
    return if (this.length > 20) {
        this.uppercase(Locale.getDefault()).substring(0, 17) + "...".uppercase(Locale.ROOT).padStart(0)
    } else if (this.length < 20) {
        this.uppercase(Locale.getDefault()).padEnd(20, ' ').uppercase(Locale.ROOT).padStart(0)
    } else {
        this.uppercase(Locale.ROOT).padStart(0)
    }
}

fun String.checkTripSecondTransport(): String {
    return if (this.length > 15) {
        this.uppercase(Locale.getDefault()).substring(0, 13) + "..".uppercase(Locale.ROOT)
            .padStart(this.length - 15)
    } else if (this.length < 15) {
        this.uppercase(Locale.getDefault()).padEnd(15, ' ').uppercase(Locale.ROOT).padStart(this.length - 15)
    } else {
        this.uppercase(Locale.ROOT).padStart(this.length - 15)
    }
}

fun String.checkQuantity(): String {
    return if (this.length < 11) {
        this.padEnd(11, ' ')
    } else {
        this
    }
}

fun String.checkAmount(): String {
    val itemNumber = "AMOUNT"
    return if (this.length <= itemNumber.length) {
        this.padStart((itemNumber.length - this.length) + this.length, ' ')
    } else {
        this
    }

}

fun String.insertNewLineBetweenWords(): String {
    val words = this.split(" ")
    return if (words.size > 1) {
        words.joinToString("\n")
    } else {
        this
    }
}

fun getDateRange(timePeriod: String?): Pair<String?, String?> {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val now = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0)
    val startDate: LocalDateTime = when (timePeriod) {
        "Today" -> now
        "This Week" -> now.minusDays((now.dayOfWeek.value - 1).toLong())
        "This Month" -> now.withDayOfMonth(1)
        "This Year" -> now.withDayOfYear(1)
        else -> return Pair(null, null)
    }
    return Pair(startDate.format(formatter), now.format(formatter))
}


fun String.replaceUnderscoreWithSpace(): String {
    return if (this.contains("_")) {
        this.replace("_", " ")
    } else {
        this
    }
}


fun String.cleanVersionCode(): String {
    //if the version code has a dash and a letter, remove the letter
    return if (this.contains("-")) {
        this.replace(Regex("-[a-zA-Z]+\$"), "")
    } else {
        this
    }


}


//fun getCustomerClassification(): String {
//    val customerClassification = logedInData?.fromJson<LoginUserResponse>()?.customerClassification
//    return customerClassification ?: ""
//}


fun String.removeCharacter(charToRemove: Char): String {
    return this.filter { it != charToRemove }
}

fun String.removeLeadingZeros(): String {
    return this.replaceFirst("^0+".toRegex(), "")
}

fun String.extractUserName(): String? {
    val token = split(".")[1]
    val decodedToken = String(Base64.decode(token, Base64.DEFAULT))
    return JSONObject(decodedToken).getString("userName")
    Timber.e("userName ${JSONObject(decodedToken).getString("userName")}")
}

fun decodeTranzwareResponse(response: String): String {
    // Convert hex string to byte array
    val byteArray = response.chunked(2).map { it.toInt(16).toByte() }.toByteArray()

    // Define the length of the header to be removed (for example, 12 bytes)
    val headerLength = 12

    // Remove the header
    val payload = byteArray.drop(headerLength).toByteArray()

    // Convert the remaining payload back to a hex string
    return payload.joinToString("") { "%02X".format(it) }
}
fun hexToBytes(hex: String): ByteArray {
    val result = ByteArray(hex.length / 2)
    for (i in result.indices) {
        val pos = i * 2
        result[i] = ((hex[pos].digitToInt(16) shl 4) +
                hex[pos + 1].digitToInt(16)).toByte()
    }
    return result
}


fun String.convertEqualToD(): String {
    return this/*.replace('=', 'D')*/
}

fun Long.toFormattedDateTime(): String {
    val date = Date(this)
    val format = SimpleDateFormat("ddMMyyyy hh:mm:ss a", Locale.getDefault())
    return format.format(date)
}

fun String.firstTwoChars(): String {
    return if (this.length >= 2) this.substring(0, 2) else this
}


fun String?.getFormattedPurchaseAmount(): String {
    return this?.toLongOrNull()?.div(100.0)?.let {
        NumberFormat.getNumberInstance(Locale.US).apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
        }.format(it)
    } ?: "0.0"
}