package cn.bakamc.common.utils.net

import com.google.gson.JsonObject
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpClient.Version
import java.net.http.HttpRequest.*
import java.net.http.HttpResponse
import java.net.http.HttpResponse.BodyHandler
import java.time.Duration
import java.util.*

/**
 *

 * 项目名 BakaMC

 * 包名 cn.bakamc.common.utils.net

 * 文件名 HttpHelp

 * 创建时间 2022/7/2 21:45

 * @author forpleuvoir

 */
@Suppress("KDocUnresolvedReference")
class HttpHelper<T>(
	private val uri: String,
	private val bodyHandler: BodyHandler<T>,
	internal var bodyPublisher: BodyPublisher = BodyPublishers.noBody(),
) {

	companion object {

		private fun JsonObject.toPairs(): List<Pair<String, String>> {
			return LinkedList<Pair<String, String>>().apply { this@toPairs.entrySet().forEach { add(it.key to it.value.toString()) } }
		}

	}

	internal val client = HttpClient.newHttpClient()

	internal val requestBuilder: Builder = newBuilder().uri(URI.create(uri)).timeout(Duration.ofSeconds(10))

	/**
	 * 设置超时 默认 10秒
	 * @param time Duration
	 */
	fun timeout(time: Duration): HttpHelper<T> {
		requestBuilder.timeout(time)
		return this
	}

	/**
	 * 设置超时 默认 10秒
	 * @param time Duration
	 */
	inline fun timeout(time: () -> Duration): HttpHelper<T> {
		return timeout(time())
	}

	/**
	 * 请求体
	 * @param bodyPublisher Function0<BodyPublisher>
	 * @return HttpHelper<T>
	 */
	fun bodyPublisher(bodyPublisher: BodyPublisher): HttpHelper<T> {
		this.bodyPublisher = bodyPublisher
		return this
	}

	/**
	 * 请求体
	 * @param bodyPublisher Function0<BodyPublisher>
	 * @return HttpHelper<T>
	 */
	inline fun bodyPublisher(bodyPublisher: () -> BodyPublisher): HttpHelper<T> {
		return bodyPublisher(bodyPublisher())
	}

	/**
	 * String请求体
	 * @param bodyPublisher Function0<BodyPublisher>
	 * @return HttpHelper<T>
	 */
	fun stringBodyPublisher(body: String): HttpHelper<T> {
		this.bodyPublisher = BodyPublishers.ofString(body)
		return this
	}

	/**
	 * String请求体
	 * @param bodyPublisher Function0<BodyPublisher>
	 * @return HttpHelper<T>
	 */
	inline fun stringBodyPublisher(body: () -> String): HttpHelper<T> {
		return stringBodyPublisher(body())
	}

	/**
	 * url参数
	 * @param params Array<out Pair<String, String>>
	 * @return HttpGetter<T>
	 */
	fun params(vararg params: Pair<String, String>): HttpHelper<T> {
		val str = StringBuilder(uri)
		str.append("?")
		params.forEachIndexed { index, pair ->
			str.append(pair.first, "=", pair.second)
			if (index != params.size - 1) str.append("&")
		}
		requestBuilder.uri(URI.create(str.toString()))
		return this
	}

	/**
	 * url参数 json对象的值会被转换为string
	 * @param params JsonObject
	 * @return HttpGetter<T>
	 */
	inline fun params(params: () -> JsonObject): HttpHelper<T> {
		return params(params())
	}

	/**
	 * url参数 json对象的值会被转换为string
	 * @param params JsonObject
	 * @return HttpGetter<T>
	 */
	fun params(params: JsonObject): HttpHelper<T> {
		params(*params.toPairs().toTypedArray())
		return this
	}

	/**
	 * 请求头
	 * @param headers Array<out Pair<String, String>>
	 * @return HttpGetter<T>
	 */
	fun headers(vararg headers: Pair<String, String>): HttpHelper<T> {
		headers.onEach {
			requestBuilder.header(it.first, it.second)
		}
		return this
	}

	/**
	 * 请求头 json对象的值会被转换为string
	 * @param headers JsonObject
	 * @return HttpGetter<T>
	 */
	inline fun headers(headers: () -> JsonObject): HttpHelper<T> {
		return headers(headers())
	}

	/**
	 * 请求头 json对象的值会被转换为string
	 * @param headers JsonObject
	 * @return HttpGetter<T>
	 */
	fun headers(headers: JsonObject): HttpHelper<T> {
		headers(*headers.toPairs().toTypedArray())
		return this
	}

	inline fun expectContinue(enable: () -> Boolean): HttpHelper<T> {
		return expectContinue(enable())
	}

	fun expectContinue(enable: Boolean): HttpHelper<T> {
		requestBuilder.expectContinue(enable)
		return this
	}

	fun version(version: Version): HttpHelper<T> {
		requestBuilder.version(version)
		return this
	}

	inline fun version(version: () -> Version): HttpHelper<T> {
		return version(version())
	}


	/**
	 * 发送同步请求
	 * @return HttpResponse<T>
	 */
	fun send(): HttpResponse<T> {
		return client.send(requestBuilder.build(), bodyHandler)
	}

	/**
	 * 发送同步请求 返回body
	 * @return T
	 */
	fun sendGetBody(): T {
		return client.send(requestBuilder.build(), bodyHandler).body()
	}

	/**
	 * 发送异步请求
	 * @param action Function1<HttpResponse<T>, Unit>
	 */
	fun sendAsync(action: (HttpResponse<T>) -> Unit) {
		client.sendAsync(requestBuilder.build(), bodyHandler)
			.thenAcceptAsync(action)
	}

	/**
	 * 发送异步请求
	 * @param action Function1<T, Unit> T ：body
	 */
	fun sendAsyncGetBody(action: (T) -> Unit) {
		client.sendAsync(requestBuilder.build(), bodyHandler)
			.thenAcceptAsync { action(it.body()) }
	}
}

