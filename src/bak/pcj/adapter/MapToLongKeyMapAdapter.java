/*
 *  Primitive Collections for Java.
 *  Copyright (C) 2003  S�ren Bak
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package bak.pcj.adapter;

import bak.pcj.Adapter;
import bak.pcj.LongIterator;
import bak.pcj.map.LongKeyMap;
import bak.pcj.map.AbstractLongKeyMap;
import bak.pcj.map.LongKeyMapIterator;
import bak.pcj.set.LongSet;
import bak.pcj.util.Exceptions;

import java.util.Map;
import java.util.Iterator;
import java.util.Collection;

/**
 *  This class represents adaptions of Java Collections Framework
 *  maps to primitive maps from long values to objects.
 *  The adapter is implemented as a wrapper around the map. 
 *  Thus, changes to the underlying map are reflected by this
 *  map and vice versa.
 *
 *  <p>
 *  Adapters from JCF maps to primitive map will
 *  fail if the JCF collection contains <tt>null</tt> keys or
 *  keys of the wrong class. However, adapters are not fast
 *  failing in the case that the underlying map should
 *  contain illegal keys. To implement fast failure would require
 *  every operation to check every key of the underlying
 *  map before doing anything. Instead validation methods
 *  are provided. They can be called using the assertion facility
 *  in the client code:
 *  <pre>
 *      MapToLongKeyMapAdapter s;
 *      ...
 *      <b>assert</b> s.validate();
 *  </pre>
 *  or by letting the adapter throw an exception on illegal values:
 *  <pre>
 *      MapToLongKeyMapAdapter s;
 *      ...
 *      s.evalidate();  // Throws an exception on illegal values
 *  </pre>
 *  Either way, validation must be invoked directly by the client
 *  code.
 *
 *  @author     S&oslash;ren Bak
 *  @version    1.2     21-08-2003 19:10
 *  @since      1.0
 */
public class MapToLongKeyMapAdapter extends AbstractLongKeyMap implements LongKeyMap {

    /** The underlying map. */
    protected Map map;

    /**
     *  Creates a new adaption to a map from long
     *  values to objects.
     *
     *  @param      map
     *              the underlying map. This map must
     *              consist of keys of class
     *              {@link Long Long}. Otherwise a
     *              {@link ClassCastException ClassCastException}
     *              will be thrown by some methods.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     */
    public MapToLongKeyMapAdapter(Map map) {
        this(map, false);
    }

    /**
     *  Creates a new adaption to a map from long
     *  values to objects. The map to adapt is optionally validated.
     *
     *  @param      map
     *              the underlying map. This map must
     *              consist of keys of class
     *              {@link Long Long}. Otherwise a
     *              {@link ClassCastException ClassCastException}
     *              will be thrown by some methods.
     *
     *  @param      validate
     *              indicates whether <tt>map</tt> should
     *              be checked for illegal keys.
     *
     *  @throws     NullPointerException
     *              if <tt>map</tt> is <tt>null</tt>.
     *
     *  @throws     IllegalStateException
     *              if <tt>validate</tt> is <tt>true</tt> and
     *              <tt>map</tt> contains a <tt>null</tt> key
     *              or a key that is not of class
     *              {@link Long Long}.
     */
    public MapToLongKeyMapAdapter(Map map, boolean validate) {
        if (map == null)
            Exceptions.nullArgument("map");
        this.map = map;
        if (validate)
            evalidate();
    }

    public void clear()
    { map.clear(); }

    public boolean containsKey(long key) {
        return map.get(new Long(key)) != null;
    }

    public boolean containsValue(Object value)
    { return map.containsValue(value); }

    public LongKeyMapIterator entries() {
        return new LongKeyMapIterator() {
            Iterator i = map.entrySet().iterator();
            Map.Entry lastEntry = null;

            public boolean hasNext()
            { return i.hasNext(); }

            public void next()
            { lastEntry = (Map.Entry)i.next(); }

            public long getKey() {
                if (lastEntry == null)
                    Exceptions.noElementToGet();
                return ((Long)lastEntry.getKey()).longValue();
            }

            public Object getValue() {
                if (lastEntry == null)
                    Exceptions.noElementToGet();
                return lastEntry.getValue();
            }

            public void remove() {
                i.remove();
                lastEntry = null;
            }
        };
    }

    public Object get(long key) {
        return map.get(new Long(key));
    }

    public LongSet keySet()
    { return new SetToLongSetAdapter(map.keySet()); }

    public Object put(long key, Object value) {
        return map.put(new Long(key), value);
    }

    public Object remove(long key) {
        return map.remove(new Long(key));
    }

    public int size()
    { return map.size(); }

    public Collection values()
    { return map.values(); }

    /**
     *  Indicates whether the underlying map is valid for
     *  this adapter. For the underlying map to be valid, it
     *  can only contain {@link Long Long} keys and no <tt>null</tt>
     *  keys.
     *
     *  @return     <tt>true</tt> if the underlying map is
     *              valid; returns <tt>false</tt> otherwise.
     */
    public boolean validate()
    { return Adapter.isLongKeyAdaptable(map); }

    /**
     *  Validates the map underlying this adapter and throws
     *  an exception if it is invalid. For the underlying map
     *  to be valid, it can only contain {@link Long Long}
     *  keys and no <tt>null</tt> keys.
     *
     *  @throws     IllegalStateException
     *              if the underlying map is invalid.
     */
    public void evalidate() {
        if (!validate())
            Exceptions.cannotAdapt("map");
    }

}