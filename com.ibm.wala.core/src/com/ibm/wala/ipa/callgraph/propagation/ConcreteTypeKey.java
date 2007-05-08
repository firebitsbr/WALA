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

import java.util.Collection;
import java.util.Iterator;

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.util.debug.Assertions;

/**
 * An instance key which represents a unique set for each concrete type
 */
public final class ConcreteTypeKey implements InstanceKey {
  private final IClass type;

  public ConcreteTypeKey(IClass type) {
    if (Assertions.verifyAssertions) {
      Assertions._assert(type != null);
      if (type.isInterface()) {
        Assertions.UNREACHABLE("unexpected interface: " + type);
      }
    }
    this.type = type;
  }

  public boolean equals(Object obj) {
    if (obj instanceof ConcreteTypeKey) {
      ConcreteTypeKey other = (ConcreteTypeKey) obj;
      return type.equals(other.type);
    } else {
      return false;
    }
  }

  public int hashCode() {
    return 461 * type.hashCode();
  }

  public String toString() {
    return "[" + type + "]";
  }

  public IClass getType() {
    return type;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ibm.wala.ipa.callgraph.propagation.InstanceKey#getConcreteType()
   */
  public IClass getConcreteType() {
    return type;
  }

  /**
   * @param pei
   *          a PEI instruction
   * @param cha
   *          governing class hierarchy
   * @return a set of ConcreteTypeKeys that represent the exceptions the PEI may
   *         throw.
   * @throws IllegalArgumentException  if pei is null
   */
  public static InstanceKey[] getInstanceKeysForPEI(SSAInstruction pei, ClassHierarchy cha) {
    if (pei == null) {
      throw new IllegalArgumentException("pei is null");
    }
    Collection types = pei.getExceptionTypes();
    // TODO: institute a cache?
    if (types == null) {
      return null;
    }
    InstanceKey[] result = new InstanceKey[types.size()];
    int i = 0;
    for (Iterator it = types.iterator(); it.hasNext(); ) {
      TypeReference type = (TypeReference)it.next();
      if (Assertions.verifyAssertions) {
        if (type == null) {
          Assertions._assert(type != null);
        }
      }
      IClass klass = cha.lookupClass(type);
      result[i++] = new ConcreteTypeKey(klass);
    }
    return result;
  }
}