package com.bitflippersanonymous.buck.domain;

import android.os.Parcel;
import android.os.Parcelable;

// Needs to preserve through Create/Destroy
public class Cut implements Parcelable {
	private float mWidth;
	private float mLength;
	
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeFloat(mWidth);
		dest.writeFloat(mLength);
	}
	
	public static final Parcelable.Creator<Cut> CREATOR = new Parcelable.Creator<Cut>() {
		public Cut createFromParcel(Parcel in) {
			return new Cut(in);
		}

		public Cut[] newArray(int size) {
			return new Cut[size];
		}
	};

	private Cut(Parcel in) {
		mWidth = in.readFloat();
		mLength = in.readFloat();
	}
			
	public Cut(float width, float length) {
		mWidth = width;
		mLength = length;
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