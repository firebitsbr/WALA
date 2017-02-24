/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.wala.ipa.slicer;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.propagation.PointerKey;
import com.ibm.wala.ssa.SSAAbstractInvokeInstruction;
import com.ibm.wala.util.json.JSONObject;

public abstract class HeapStatement extends Statement {

  private final PointerKey loc;

  public HeapStatement(CGNode node, PointerKey loc) {

    super(node);
    
    if (loc == null) {
      throw new IllegalArgumentException("loc is null");
    }
    this.loc = loc;
  }


  public final static class HeapParamCaller extends HeapStatement {
    // index into the IR instruction array of the call statements
    private final int callIndex;

    public HeapParamCaller(CGNode node,int callIndex, PointerKey loc) {
      super(node, loc);
      this.callIndex = callIndex;
    }

    @Override
    public Kind getKind() {
      return Kind.HEAP_PARAM_CALLER;
    }

    public int getCallIndex() {
      return callIndex;
    }
    
    public SSAAbstractInvokeInstruction getCall() {
      return (SSAAbstractInvokeInstruction) getNode().getIR().getInstructions()[callIndex];
    }
    
    @Override
    public String toString() {
      return getKind().toString() + ":" + getNode() + " " + getLocation() + " call:" + getCall();
    }

    @Override
    public int hashCode() {
      return getLocation().hashCode() + 4289 * callIndex + 4133 * getNode().hashCode() + 8831;
    }

    @Override
    public boolean equals(Object obj) {
      // instanceof is OK because this class is final.  instanceof is more efficient than getClass
      if (obj instanceof HeapParamCaller) {
        HeapParamCaller other = (HeapParamCaller) obj;
        return getNode().equals(other.getNode()) && getLocation().equals(other.getLocation()) && callIndex == other.callIndex;
      } else {
        return false;
      }
    }

    @Override
    public JSONObject toJSON() {
      //TODO
      return super.toJSON();
      //return getKind().toString() + "," + getNode().getMethod().getSignature() + "," + getLocation().toJson() + "," + getCall();
    }
  }

  public final static class HeapParamCallee extends HeapStatement {
    public HeapParamCallee(CGNode node, PointerKey loc) {
      super(node, loc);
    }

    @Override
    public Kind getKind() {
      return Kind.HEAP_PARAM_CALLEE;
    }
    
    @Override
    public int hashCode() {
      return getLocation().hashCode() + 7727 * getNode().hashCode() + 7841;
    }

    @Override
    public boolean equals(Object obj) {
      // instanceof is ok because this class is final.  instanceof is more efficient than getClass
      if (obj instanceof HeapParamCallee) {
        HeapParamCallee other = (HeapParamCallee) obj;
        return getNode().equals(other.getNode()) && getLocation().equals(other.getLocation());
      } else {
        return false;
      }
    }
    
    @Override
    public String toString() {
      return getKind().toString() + ":" + getNode() + " " + getLocation();
    }

    @Override
    public JSONObject toJSON() {
      //TODO
      return super.toJSON();
    }
  }

  public final static class HeapReturnCaller extends HeapStatement {
    // index into the instruction array of the relevant call instruction
    private final int callIndex;
//    private final SSAAbstractInvokeInstruction call;

    public HeapReturnCaller(CGNode node, int callIndex, PointerKey loc) {
      super(node, loc);
      this.callIndex = callIndex;
    }

    @Override
    public Kind getKind() {
      return Kind.HEAP_RET_CALLER;
    }

    public int getCallIndex() {
      return callIndex;
    }
    
    public SSAAbstractInvokeInstruction getCall() {
      return (SSAAbstractInvokeInstruction) getNode().getIR().getInstructions()[callIndex];
    }

    @Override
    public String toString() {
      return getKind().toString() + ":" + getNode() + " " + getLocation() + " call:" + getCall();
    }

    @Override
    public int hashCode() {
      return getLocation().hashCode() + 8887 * callIndex + 8731 * getNode().hashCode() + 7919;
    }

    @Override
    public boolean equals(Object obj) {    
      // instanceof is ok because this class is final.  instanceof is more efficient than getClass
      if (obj instanceof HeapReturnCaller) {
        HeapReturnCaller other = (HeapReturnCaller) obj;
        return getNode().equals(other.getNode()) && getLocation().equals(other.getLocation()) && callIndex == other.callIndex;
      } else {
        return false;
      }
    }

    @Override
    public JSONObject toJSON() {
      //TODO
      return super.toJSON();
    }
  }

  public final static class HeapReturnCallee extends HeapStatement {
    public HeapReturnCallee(CGNode node, PointerKey loc) {
      super(node, loc);
    }

    @Override
    public Kind getKind() {
      return Kind.HEAP_RET_CALLEE;
    }
    
    @Override
    public int hashCode() {
      return getLocation().hashCode() + 9533 * getNode().hashCode() + 9631;
    }

    @Override
    public boolean equals(Object obj) {
      // instanceof is ok because this class is final.  instanceof is more efficient than getClass
      if (obj instanceof HeapReturnCallee) {
        HeapReturnCallee other = (HeapReturnCallee) obj;
        return getNode().equals(other.getNode()) && getLocation().equals(other.getLocation());
      } else {
        return false;
      }
    }
    
    @Override
    public String toString() {
      return getKind().toString() + ":" + getNode() + " " + getLocation();
    }

    @Override
    public JSONObject toJSON() {
      //TODO
      return super.toJSON();
    }
  }

  public PointerKey getLocation() {
    return loc;
  }
}
