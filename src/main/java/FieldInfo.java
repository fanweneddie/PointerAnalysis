package core;

import java.util.HashMap;
import java.util.HashSet;

import soot.Local;
import soot.SootField;


// stores the info of each field for field-sensitive analysis
public class FieldInfo implements Cloneable {
	// the potential source of the local variable
	private HashSet<Integer> localSourceSet;
	// the potential source of fields 
	private HashMap<SootField, HashSet<Integer>> fieldSourceMap;
	
	FieldInfo() {
		this.localSourceSet = new HashSet<>();
		this.fieldSourceMap = new HashMap<>();
	}

	FieldInfo(HashSet<Integer> localSourceSet, 
			HashMap<SootField, HashSet<Integer>> fieldSourceMap) {
		this.localSourceSet = new HashSet<>();
		this.fieldSourceMap = new HashMap<>();
		this.localSourceSet = localSourceSet;
		this.fieldSourceMap = fieldSourceMap;
	}

	@Override  
	public FieldInfo clone() {
		try { 
			FieldInfo fi = (FieldInfo) super.clone();
			fi.SetLocalSourceSet((HashSet<Integer>)(this.localSourceSet.clone()));
			//fi.SetLocalSourceSet(this.localSourceSet.clone());
			fi.SetFieldSourceMap((HashMap<SootField, HashSet<Integer>>)
					(this.fieldSourceMap.clone()));
			return fi;
		} catch (CloneNotSupportedException e) {
			return new FieldInfo(this.localSourceSet, this.fieldSourceMap);
		}
	}

	// add the source of the local variable
	public void AddLocalSourceSet(Integer source){
		localSourceSet.add(source);
    }

    // copy the input sourceSet to the local variable 
    public void CopyLocalSourceSet(HashSet<Integer> sourceSet) {
        this.localSourceSet.addAll(sourceSet);
    }

	// add a field for this local variable
	public void AddField(SootField field) {
		if ( !fieldSourceMap.containsKey(field) ) {
			HashSet<Integer> sourceSet = new HashSet<>();
			fieldSourceMap.put(field, sourceSet);
		}
	}

	// add the field and the source it may come from
	public void AddFieldSourceMap(SootField field, Integer source) {
		if ( !fieldSourceMap.containsKey(field) ) {
			HashSet<Integer> sourceSet = new HashSet<>();
			sourceSet.add(source);
			fieldSourceMap.put(field, sourceSet);
		}
		else {
			fieldSourceMap.get(field).add(source);
		}
    }
    
    // copy the input sourceSet into field
    public void CopyFieldSourceMap(SootField field, HashSet<Integer> sourceSet) {
        if (!fieldSourceMap.containsKey(field)) {
			HashSet<Integer> newSourceSet = (HashSet<Integer>)(sourceSet.clone());
			fieldSourceMap.put(field, newSourceSet);
		}
		else {
			fieldSourceMap.get(field).addAll(sourceSet);
		}
	}

	// get the source set of the local variable
	public HashSet<Integer> GetLocalSourceSet() {
		return localSourceSet;
	}

	// get the source set of the field
	public HashSet<Integer> GetFieldSourceSet(SootField field) {
		if (fieldSourceMap.containsKey(field))
			return fieldSourceMap.get(field);
		else
			return null;
	}

	public HashMap<SootField, HashSet<Integer>> GetFieldSourceMap() {
		return fieldSourceMap;
	}

	public void SetLocalSourceSet(HashSet<Integer> localSourceSet) {
		this.localSourceSet = localSourceSet;
	}

	public void SetFieldSourceMap(HashMap<SootField, HashSet<Integer>> fieldSourceMap) {
		this.fieldSourceMap = fieldSourceMap;
	}

	public void RemoveField(SootField field) {
		fieldSourceMap.remove(field);
	}
}