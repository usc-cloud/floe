/*
 * Copyright 2011, University of Southern California. All Rights Reserved.
 * 
 * This software is experimental in nature and is provided on an AS-IS basis only. 
 * The University SPECIFICALLY DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT 
 * LIMITATION ANY WARRANTY AS TO MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * This software may be reproduced and used for non-commercial purposes only, 
 * so long as this copyright notice is reproduced with each such copy made.
 */
package edu.usc.pgroup.floe.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.lang.reflect.Proxy;
import java.util.HashMap;


/**
 * <code>ClassLoadingAwareObjectInputStream</code> is an Extention of Object input stream to be used
 * to de-serialize JMS Object Messages.
 *
 * <p>This was introduced to resolve the class loading issues which can happen when we use the client
 * libraries in a complex class loading Environment.</p>
 */
public class ClassLoadingAwareObjectInputStream extends ObjectInputStream
{
    /** <p>Class loader instance which loaded this class.
     * It will be used to load classes when we failed to load classes from dynamic class loading</p> */
    private static final ClassLoader _ON_FAULT_CLASS_LOADER =
            ClassLoadingAwareObjectInputStream.class.getClassLoader();

    /** <p>Maps primitive type names to corresponding class objects.</p> */
    private static final HashMap<String, Class> _primitives = new HashMap<String, Class>(8, 1.0F);


    public ClassLoadingAwareObjectInputStream(InputStream in) throws IOException
    {
        super(in);
    }

    @Override
    protected Class resolveClass(ObjectStreamClass classDesc)
            throws IOException, ClassNotFoundException
    {

        // Here we use TTCL as our primary class loader to load the classes
        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        return load(classDesc.getName(), cl);
    }

    @Override
    protected Class resolveProxyClass(String[] interfaces)
            throws IOException, ClassNotFoundException
    {
        // Here we use TTCL as our primary class loader to load the classes
        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        Class[] cinterfaces = new Class[interfaces.length];
        for (int i = 0; i < interfaces.length; i++)
        {
            cinterfaces[i] = load(interfaces[i], cl);
        }

        try
        {
            return Proxy.getProxyClass(cinterfaces[0].getClassLoader(), cinterfaces);
        }
        catch (IllegalArgumentException e)
        {
            throw new ClassNotFoundException(null, e);
        }
    }

    /**
     * <p>
     * Method we used to load class that are needed to de-serialize the objects. </p>
     * <p>
     * Here we first look up for the objects from the given class loader and if its not there
     * we will be using the class loader of this class.
     * </p>
     * @param className Class name to lookup
     * @param cl primary class loader which we 1st use to lookup
     * @return Class instance we are looking for
     * @throws ClassNotFoundException if both primary and secondary lockup's failed.
     */
    private Class load(String className, ClassLoader cl)
            throws ClassNotFoundException
    {
        try
        {
            return Class.forName(className, false, cl);
        }
        catch (ClassNotFoundException e)
        {
            final Class clazz = _primitives.get(className);

            if (clazz != null)
            {
                return clazz;
            }
            else
            {
                return Class.forName(className, false, _ON_FAULT_CLASS_LOADER);
            }
        }
    }

    static
    {
        _primitives.put("boolean", boolean.class);
        _primitives.put("byte", byte.class);
        _primitives.put("char", char.class);
        _primitives.put("short", short.class);
        _primitives.put("int", int.class);
        _primitives.put("long", long.class);
        _primitives.put("float", float.class);
        _primitives.put("double", double.class);
        _primitives.put("void", void.class);
    }
}
