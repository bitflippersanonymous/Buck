package com.bitflippersanonymous.buck.domain;

import android.os.Parcel;
import android.os.Parcelable;

// Needs to preserve through Create/Destroy
public class Cut implements Parcelable {
	private float mWidth;
	private float mLength;
	
	// FIXME: impl these
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
	}
	public float getWidth() {
		return mWidth;
	}
	public void setWidth(float mWidth) {
		this.mWidth = mWidth;
	}
	public float getLength() {
		return mLength;
	}
	public void setLength(float mLength) {
		this.mLength = mLength;
	}
}