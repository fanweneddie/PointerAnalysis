package core;

import java.util.HashMap;
import java.util.HashSet;

import soot.Local;
import soot.SootField;


// stores the info of all local variables
public class LocalInfo implements Cloneable {
	// maps the local to their field info
	private HashMap<Local, FieldInfo> localField;

	LocalInfo() {
		this.localField = new HashMap<>();
	}

	LocalInfo(HashMap<Local, FieldInfo> localField) {
		this.localField = new HashMap<>();
		this.localField = (HashMap<Local, FieldInfo>)(localField.clone());
	}

	@Override
	public LocalInfo clone() {
		try {
			LocalInfo li = (LocalInfo) super.clone();
			li.SetLocalField( (HashMap<Local, FieldInfo>)(this.localField.clone()) );
			return li;
		} catch (CloneNotSupportedException e) {
			return new LocalInfo(this.localField);
		}
	}

	// add a local variable and its source
	public void AddLocal(Local base, Integer source) {
		if(!localField.containsKey(base)) {
			FieldInfo fi = new FieldInfo();
			fi.AddLocalSourceSet(source);
			localField.put(base, fi);
		}
		else {
			localField.get(base).AddLocalSourceSet(source);
		}
	}

	// add a source of the field of the local variable
	public void AddLocalFieldSourceSet(Local base, 
							SootField field, Integer source) {
		if (!localField.containsKey(base)) {
			FieldInfo fi = new FieldInfo();
			fi.AddFieldSourceMap(field, source);
			localField.put(base,fi);
		}
		else {
			localField.get(base).AddFieldSourceMap(field, source);
		}
	}

	// get the info of its field for a local
	public FieldInfo GetFieldInfo(Local base) {
		if (localField.containsKey(base))
			return localField.get(base);
		else
			return null;
	}

	public HashMap<Local, FieldInfo> GetLocalField() {
		return localField;
	}
	
	public void SetLocalField(HashMap<Local, FieldInfo> localField) {
		this.localField = localField;
	}
    
    // update the source of the local variable
    public void UpdateLocalSource(Local base, HashSet<Integer> localSourceSet) {
        if(!localField.containsKey(base)) {
            FieldInfo fi = new FieldInfo();
            HashSet<Integer> local_source_set = (HashSet<Integer>)(localSourceSet.clone());
            fi.CopyLocalSourceSet(localSourceSet);
            localField.put(base,fi);
		}
		else {
			localField.get(base).CopyLocalSourceSet(localSourceSet);
		}
    }

    // update the source of the field of the local variable
    public void UpdateLocalFieldSource(Local base, SootField field, 
                HashSet<Integer> fieldSourceSet) {
        if(!localField.containsKey(base)) {
            FieldInfo fi = new FieldInfo();
            HashSet<Integer> field_source_set = fieldSourceSet;
            fi.CopyFieldSourceMap(field,fieldSourceSet);
            localField.put(base,fi);
        }
        else {
            localField.get(base).CopyFieldSourceMap(field, fieldSourceSet);
        }
	}

	// remove a local from localField
	public void RemoveLocal(Local local) {
		localField.remove(local);
	}

	// remove a field of a local from localField
	public void RemoveLocalField(Local base, SootField field) {
		localField.get(base).RemoveField(field);
	}

	// remove all fields for a local
	public void RemoveAllLocalField(Local base) {
		FieldInfo fieldInfo = localField.get(base);
		HashMap<SootField, HashSet<Integer>> fieldSourceMap = fieldInfo.GetFieldSourceMap();
		for(HashMap.Entry<SootField, HashSet<Integer>> entry : fieldSourceMap.entrySet()) {
			RemoveLocalField(base, entry.getKey());
		}
	}

}