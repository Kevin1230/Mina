package com.komori.wu.mina;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class GsonUtil {
	public static <T> T parseJsonWithGson(String jsonData, Class<T> type) {
		Gson gson = new Gson();
		T result = gson.fromJson(jsonData, type);
		return result;
	}

	public static <T> List<T> parseJsonArrayWithGson(String jsonData,
			Class<T> type) {
		Gson gson = new Gson();
		List<T> result = gson.fromJson(jsonData, new TypeToken<List<T>>() {
		}.getType());
		return result;
	}
}
