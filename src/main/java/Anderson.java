package core;

import java.util.HashMap;
import java.util.HashSet;

import soot.Local;
import soot.Value;
import soot.SootField;
import soot.jimple.InstanceFieldRef;


// the implementation of Anderson Algorithm
public class Anderson implements Cloneable{
	
	// the info of local variables
	private LocalInfo localInfo;

	Anderson() {
		this.localInfo = new LocalInfo();
	}

	Anderson(LocalInfo localInfo) {
		this.localInfo = new LocalInfo();
		this.localInfo = localInfo;
	}

	public LocalInfo GetLocalInfo() {
		return localInfo;
	}

	public void SetLocalInfo(LocalInfo localInfo) {
		this.localInfo = localInfo;
	}

	@Override
	public Anderson clone() throws CloneNotSupportedException {
		try {
			Anderson anderson = (Anderson) super.clone();
        	anderson.localInfo = this.localInfo.clone();
			return anderson;
		} catch (CloneNotSupportedException e) {
			return new Anderson(this.localInfo);
		}
	}

	// add a new local and its source
	public void AddNewLocal(Local base, Integer source) {
		localInfo.AddLocal(base, source);
	}
	
	// add a new field of a local and its source.
	public void AddNewField(Local base, SootField field, Integer source) {
		localInfo.AddLocalFieldSourceSet(base, field, source);
	}


	// assign a local to local in this localinfo(e.g. to = from)
	public void AssignLocalToLocal(Local to, Anderson andersonFrom,Local from) {
		// update local source set for 'to'
		HashSet<Integer> localSourceSetFrom
			= andersonFrom.GetLocalInfo().GetFieldInfo(from).GetLocalSourceSet();
		localInfo.UpdateLocalSource(to, localSourceSetFrom);

		// update field source map for 'to'
		HashMap<SootField, HashSet<Integer>> fieldSourceMapFrom
			= andersonFrom.GetLocalInfo().GetFieldInfo(from).GetFieldSourceMap();
		for(HashMap.Entry<SootField, HashSet<Integer>> entry : fieldSourceMapFrom.entrySet()) {
			localInfo.UpdateLocalFieldSource(to, entry.getKey(), entry.getValue());
		}
	}

	// assign a local to field in this localinfo(e.g. to.f = from)
	public void AssignLocalToField(Local to, SootField toField,
				Anderson andersonFrom, Local from) {
		// update field source set for 'to'

		// for locals in "from"
		HashSet<Integer> localSourceSetFrom
			= andersonFrom.GetLocalInfo().GetFieldInfo(from).GetLocalSourceSet();
		localInfo.UpdateLocalFieldSource(to, toField, localSourceSetFrom);

		// for fields in "from"
		HashMap<SootField, HashSet<Integer>> fieldSourceMapFrom
			= andersonFrom.GetLocalInfo().GetFieldInfo(from).GetFieldSourceMap();
		for(HashMap.Entry<SootField, HashSet<Integer>> entry : fieldSourceMapFrom.entrySet()) {
			localInfo.UpdateLocalFieldSource(to, toField, entry.getValue());
		}
	}

	// assign a field to local(e.g. to = from.f)
	public void AssignFieldToLocal(Local to, Anderson andersonFrom, 
				Local from, SootField fromField) {
		// update local source set for 'to'
		HashMap<SootField, HashSet<Integer>> fieldSourceMapFrom
			= andersonFrom.GetLocalInfo().GetFieldInfo(from).GetFieldSourceMap();
		HashSet<Integer> fieldSourceSet = fieldSourceMapFrom.get(fromField);
		localInfo.UpdateLocalSource(to, fieldSourceSet);
		// delete the field set for 'to'
		localInfo.RemoveAllLocalField(to);
	}

	// assign a field to field(e.g. to.f1 = from.f2)
	public void AssignFieldToField(Local to, SootField toField, 
				Anderson andersonFrom, Local from, SootField fromField) {
		// update field source set for 'to'
		HashMap<SootField, HashSet<Integer>> fieldSourceMapFrom
			= andersonFrom.GetLocalInfo().GetFieldInfo(from).GetFieldSourceMap();
		HashSet<Integer> fieldSourceSet = fieldSourceMapFrom.get(fromField);
		localInfo.UpdateLocalFieldSource(to, toField, fieldSourceSet);
	}

	// delete a local(with its field) in LocalInfo
	public void DeleteLocal(Local local) {
		localInfo.RemoveLocal(local);
	}

	public void DeleteField(Local base, SootField field) {
		localInfo.RemoveLocalField(base, field);
	}

	// make a union for the input
	public void UnionMerge(Anderson in1, Anderson in2) {
		this.localInfo = new LocalInfo();
		LocalInfo li1 = in1.GetLocalInfo().clone();
		LocalInfo li2 = in2.GetLocalInfo().clone();
		this.SetLocalInfo(li1);
		HashMap<Local, FieldInfo> lf2 = li2.GetLocalField();

		// union operation
		for (HashMap.Entry<Local, FieldInfo> liEntry : lf2.entrySet()) {
			Local base = liEntry.getKey();
			FieldInfo fi = liEntry.getValue();
			HashMap<SootField, HashSet<Integer>> fsm = fi.GetFieldSourceMap();

			// copy localSourceSet
			localInfo.UpdateLocalSource(base, fi.GetLocalSourceSet());
			// copy fieldSourceMap field by field
			for (HashMap.Entry<SootField, HashSet<Integer>> fsmEntry : fsm.entrySet()) {
				localInfo.UpdateLocalFieldSource(base, fsmEntry.getKey(), fsmEntry.getValue());
			}
		}
	}

	// print the info of this
	public void Output() {
		for (HashMap.Entry<Local, FieldInfo> liEntry : localInfo.GetLocalField().entrySet()) {
			Local base = liEntry.getKey();
			FieldInfo fieldInfo = liEntry.getValue();
			HashSet<Integer> localSourceSet = fieldInfo.GetLocalSourceSet();
			HashMap<SootField, HashSet<Integer>> fieldSourceMap = fieldInfo.GetFieldSourceMap();
			
			System.out.println(base);
			System.out.print("--");
			for (Integer localSource : localSourceSet) {
				System.out.print(localSource + " ");
			}
			System.out.print("\n");

			for (HashMap.Entry<SootField, HashSet<Integer>> fsmEntry : fieldSourceMap.entrySet()) {
				HashSet<Integer> fieldSourceSet = fsmEntry.getValue();
				System.out.println("--" + fsmEntry.getKey());
				System.out.print("----");
				for( Integer fieldSource : fieldSourceSet) {
					System.out.print(fieldSource + " ");
				}
				System.out.print("\n");
			}
		}
	}

	// get the potential souceset of a variable(local or field)
	public HashSet<Integer> GetSourseSet(Value v) {

		HashSet<Integer> sourceSet = new HashSet<>();
		// v is a local
		if (v instanceof Local) {
			
			HashSet<Integer> localSourceSet 
				= localInfo.GetLocalField().get((Local)v).GetLocalSourceSet();
			HashMap<SootField, HashSet<Integer>> fieldSourceMap
				= localInfo.GetLocalField().get((Local)v).GetFieldSourceMap();
			// union with local source set and all field source set
			sourceSet.addAll(localSourceSet);
			for (HashMap.Entry<SootField, HashSet<Integer>> fsmEntry : fieldSourceMap.entrySet()) {
				sourceSet.addAll(fsmEntry.getValue());
			}
		}
		// v is a field
		else if(v instanceof InstanceFieldRef) {
			Value base = ((InstanceFieldRef)v).getBase();
			SootField field = ((InstanceFieldRef)v).getField();
			HashMap<SootField, HashSet<Integer>> fieldSourceMap
				= localInfo.GetLocalField().get((Local)base).GetFieldSourceMap();
			// union only with that field source set
			sourceSet.addAll(fieldSourceMap.get(field));
		}
		return sourceSet;
	}
	
}
