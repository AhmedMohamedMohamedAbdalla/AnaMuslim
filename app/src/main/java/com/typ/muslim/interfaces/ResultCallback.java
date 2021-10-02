/*
 * This app is developed by AHMED SLEEM
 *
 * Copyright (c) 2021.  TYP INC. All Rights Reserved
 */

package com.typ.muslim.interfaces;

public interface ResultCallback<T> {

	void onResult(T result);

	default void onFailed() {}

}
