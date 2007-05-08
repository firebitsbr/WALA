/*******************************************************************************
 * Copyright (c) 2002 - 2006 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.wala.ipa.callgraph.propagation;

import com.ibm.wala.analysis.reflection.Malleable;
import com.ibm.wala.classLoader.ArrayClass;
import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.NewSiteReference;
import com.ibm.wala.classLoader.ProgramCounter;
import com.ibm.wala.ipa.callgraph.AnalysisOptions;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.util.debug.Assertions;
import com.ibm.wala.util.debug.Trace;
import com.ibm.wala.util.warnings.ResolutionFailure;
import com.ibm.wala.util.warnings.WarningSet;

/**
 * 
 * This class provides Instance Key call backs where each instance is in the
 * same equivalence class as all other instances of the same concrete type.
 * 
 * @author sfink
 */
public class ClassBasedInstanceKeys implements InstanceKeyFactory {

  private final static boolean DEBUG = false;

  private final WarningSet warnings;

  private final AnalysisOptions options;

  private final ClassHierarchy cha;

  public ClassBasedInstanceKeys(AnalysisOptions options, ClassHierarchy cha, WarningSet warnings) {
    this.cha = cha;
    this.options = options;
    this.warnings = warnings;
  }


  public InstanceKey getInstanceKeyForAllocation(CGNode node, NewSiteReference allocation) {
    if (allocation == null) {
      throw new IllegalArgumentException("allocation is null");
    }
    if (Malleable.isMalleable(allocation.getDeclaredType())) {
      return null;
    }
    IClass type = options.getClassTargetSelector().getAllocatedTarget(node, allocation);
    if (type == null) {
      warnings.add(ResolutionFailure.create(node, allocation));
      return null;
    }

    ConcreteTypeKey key = new ConcreteTypeKey(type);

    return key;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ibm.wala.ipa.callgraph.propagation.InstanceKeyFactory#getInstanceKeyForMultiNewArray(com.ibm.wala.ipa.callgraph.CGNode,
   *      com.ibm.wala.classLoader.NewSiteReference, int)
   * 
   * dim == 0 represents the first dimension, e.g., the [Object; instances in
   * [[Object; e.g., the [[Object; instances in [[[Object; dim == 1 represents
   * the second dimension, e.g., the [Object instances in [[[Object;
   */
  public InstanceKey getInstanceKeyForMultiNewArray(CGNode node, NewSiteReference allocation, int dim) {
    if (DEBUG) {
      Trace.println("getInstanceKeyForMultiNewArray " + allocation + " " + dim);
    }
    ArrayClass type = (ArrayClass) options.getClassTargetSelector().getAllocatedTarget(node, allocation);
    assert (type != null);
    if (DEBUG) {
      Trace.println("type: " + type);
    }
    if (Assertions.verifyAssertions) {
      if (type == null) {
        Assertions._assert(type != null, "null type for " + allocation);
      }
    }
    int i = 0;
    while (i <= dim) {
      i++;
      if (Assertions.verifyAssertions && type == null) {
        Assertions.UNREACHABLE();
      }
      type = (ArrayClass) type.getElementClass();
      if (DEBUG) {
        Trace.println("intermediate: " + i + " " + type);
      }
    }
    if (DEBUG) {
      Trace.println("final type: " + type);
    }
    if (type == null) {
      return null;
    }
    ConcreteTypeKey key = new ConcreteTypeKey(type);

    return key;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ibm.detox.ipa.underConstruction.DataflowCallGraphBuilder#getInstanceKeyForStringConstant(java.lang.String)
   */
  public InstanceKey getInstanceKeyForConstant(Object S) {
    if (!options.hasConstantType(S)) {
      return null;
    } else {
      if (options.getUseConstantSpecificKeys()) {
        return new ConstantKey(S, cha.lookupClass(options.getConstantType(S)));
      } else
        return new ConcreteTypeKey(cha.lookupClass(options.getConstantType(S)));
    }
  }

  public String getStringConstantForInstanceKey(InstanceKey I) {
    if (I instanceof StringConstantKey)
      return ((StringConstantKey) I).getString();
    else
      return null;
  }

  /**
   * @return a set of ConcreteTypeKeys that represent the exceptions the PEI may
   *         throw.
   */
  public InstanceKey getInstanceKeyForPEI(CGNode node, ProgramCounter peiLoc, TypeReference type) {
    return new ConcreteTypeKey(cha.lookupClass(type));
  }

  public InstanceKey getInstanceKeyForClassObject(TypeReference type) {
    return new ConcreteTypeKey(cha.lookupClass(TypeReference.JavaLangClass));
  }

  /**
   * @return Returns the class hierarchy.
   */
  public ClassHierarchy getClassHierarchy() {
    return cha;
  }

}
