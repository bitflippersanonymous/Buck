package com.bitflippersanonymous.buck.domain;

import java.io.IOException;
import java.util.Comparator;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.JsonWriter;
import android.util.JsonReader;


// Needs to preserve through Create/Destroy
public class Dimension implements Parcelable {
	private int mWidth;  // FIXME: not sure if want these to be floats.
	private int mLength;
	
	public Dimension(int width, int length) {
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
	
	@Override
	public int hashCode() {
		return mWidth ^ mLength;
	}
	
	@Override
	public boolean equals(Object o) {
		Dimension that = (Dimension)o;
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
	
	public static final Parcelable.Creator<Dimension> CREATOR = new Parcelable.Creator<Dimension>() {
		public Dimension createFromParcel(Parcel in) {
			return new Dimension(in);
		}

		public Dimension[] newArray(int size) {
			return new Dimension[size];
		}
	};
	
	private Dimension(Parcel in) {
		mWidth = in.readInt();
		mLength = in.readInt();
	}
	
  public void writeJson(JsonWriter writer) throws IOException {
    writer.beginArray();
  	writer.value(mWidth);
    writer.value(mLength);
    writer.endArray();
  }
  
  public static Dimension readJson(JsonReader reader) throws IOException {
    reader.beginArray();
  	int width = reader.nextInt();
    int length = reader.nextInt();
    reader.endArray();
    return new Dimension(width, length);
  }
				
	private static Comparator<? super Dimension> sByLength = null;
	public static Comparator<? super Dimension> getByLength() {
		if ( sByLength == null )
			sByLength = new Comparator<Dimension>(){
			@Override
			public int compare(Dimension lhs, Dimension rhs) {
				return lhs.mLength - rhs.mLength;
			}};
		return sByLength;
	}
	
	private static Comparator<? super Dimension> sByWidth = null;
	public static Comparator<? super Dimension> getByWidth() {
		if ( sByWidth == null )
			sByWidth = new Comparator<Dimension>(){
			@Override
			public int compare(Dimension lhs, Dimension rhs) {
				return lhs.mWidth - rhs.mWidth;
			}};
		return sByWidth;
	}

}