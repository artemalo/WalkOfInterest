package sfedu.ictis.walkOfInterest.data.util

import org.json.JSONObject
import retrofit2.Response
import sfedu.ictis.walkOfInterest.domain.exception.RateLimitException
import sfedu.ictis.walkOfInterest.domain.exception.ServerException

fun Response<*>.toException(fallback: String): Exception {
    val rawError = errorBody()?.string()
    val parsedMessage = rawError?.let { tryParseFirstJsonValue(it) }

    return when (code()) {
        429 -> {
            val retryAfter = headers()["Retry-After"]?.toLongOrNull()
            RateLimitException(
                message = parsedMessage ?: "Слишком много запросов",
                retryAfterSeconds = retryAfter
            )
        }
        in 400..499 -> ServerException(parsedMessage ?: fallback)
        else -> Exception("$fallback (код ${code()})")
    }
}

fun tryParseFirstJsonValue(raw: String): String? = runCatching {
    val json = JSONObject(raw)
    val keys = json.keys()
    if (keys.hasNext()) json.getString(keys.next()) else null
}.getOrNull()
