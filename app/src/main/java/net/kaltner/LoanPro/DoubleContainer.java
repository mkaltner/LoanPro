package net.kaltner.LoanPro;

import android.content.Context;

public class DoubleContainer {
	private Double _calculatedValue;
	private boolean _isCalculated;
	private boolean _hasValue;
	private Double _value;
	private boolean _isLocked;
	private Context _activity;
	private String _name;
	
	public DoubleContainer(Context activity, String name) {
		_value = 0.0d;
		_calculatedValue = 0.0d;
		_hasValue = false;
		_isCalculated = false;
		_isLocked = false;
		_name = name;
		_activity = activity;
		
		String calculatedValue = Utils.getPreferenceString(activity, "calculatedValue");
		double value = (double)Utils.getPreferenceFloat(activity, name);
		
		if (value != Float.MIN_VALUE) {
			if (calculatedValue.equals(name)) {
				_calculatedValue = value;
				_isCalculated = true;
			}
			else {
				_value = value;
				_hasValue = true;
			}
			
			_isLocked = Utils.getPreferenceBoolean(activity, name + "Locked");
		}
	}
	
	public Double getValue() {
		double value = 0.0d;
		if (isCalculated()) {
			value = _calculatedValue;
		}
		else if (hasValue()) {
			value = _value;
		}
		return value;
	}
	
	public void setValue(Double value) {
		_value = value;
		_hasValue = true;
		
		_calculatedValue = 0.0d;
		_isCalculated = false;
		_isLocked = false;
		
		Utils.savePreferenceFloat(_activity, _name, _value.floatValue());
	}
	
	public void setValue(Double value, boolean isLocked) {
		_value = value;
		_hasValue = true;
		
		_calculatedValue = 0.0d;
		_isCalculated = false;
		_isLocked = isLocked;
		
		Utils.savePreferenceFloat(_activity, _name, _value.floatValue());
		Utils.savePreferenceBoolean(_activity, _name + "Locked", isLocked);
	}
	
	public void setUserValue(Double value) {
		_value = value;
		_hasValue = true;
		
		_calculatedValue = 0.0d;
		_isCalculated = false;
		_isLocked = true;
		
		Utils.savePreferenceFloat(_activity, _name, _value.floatValue());
		Utils.savePreferenceBoolean(_activity, _name + "Locked", true);
	}
	
	public void setCalculatedValue(Double value) {
		_calculatedValue = value;
		_isCalculated = true;
		
		_value = 0.0d;
		_hasValue = false;
		_isLocked = false;
		
		Utils.savePreferenceFloat(_activity, _name, _calculatedValue.floatValue());
		Utils.savePreferenceString(_activity, "calculatedValue", _name);
		Utils.savePreferenceBoolean(_activity, _name + "Locked", false);
	}
	
	public boolean hasValue() {
		return _hasValue;
	}
	
	public boolean isCalculated() {
		return _isCalculated;
	}
	
	public boolean isLocked() {
		return _isLocked;
	}
	
	public void reset() {
		if (_isCalculated) {
			Utils.savePreferenceString(_activity, "calculatedValue", "none");
		}
		
		_value = 0.0d;
		_calculatedValue = 0.0d;
		_hasValue = false;
		_isCalculated = false;
		_isLocked = false;
		
		Utils.savePreferenceFloat(_activity, _name, Float.MIN_VALUE);
		Utils.savePreferenceBoolean(_activity, _name + "Locked", false);
	}
	
	public boolean isEmpty() {
		return (_hasValue == false && _isCalculated == false);
	}
}
