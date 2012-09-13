package com.bitflippersanonymous.buck.domain;

import android.os.Parcel;
import android.os.Parcelable;

// Needs to preserve through Create/Destroy
public class Cut implements Parcelable {
	private int mWidth;  // FIXME: not sure if want these to be floats.
	private int mLength;
	
	@Override
	public int hashCode() {
		return mWidth ^ mLength;
	}
	
	@Override
	public boolean equals(Object o) {
		Cut that = (Cut)o;
		return mWidth == that.mWidth && mLength == that.mLength;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(mWidth);
		dest.writeInt(mLength);
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
		mWidth = in.readInt();
		mLength = in.readInt();
	}
			
	public Cut(int width, int length) {
		mWidth = width;
		mLength = length;
	}
	public int getWidth() {
		return mWidth;
	}
	public void setWidth(int mWidth) {
		this.mWidth = mWidth;
	}
	public int getLength() {
		return mLength;
	}
	public void setLength(int mLength) {
		this.mLength = mLength;
	}
}