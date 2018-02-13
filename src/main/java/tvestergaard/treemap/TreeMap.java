package tvestergaard.treemap;

import java.util.*;

public class TreeMap<K, V> implements Map<K, V>
{

	/**
	 * The comparator used when comparing the keys in the {@link TreeMap}. If <code>null</code> keys are not allowed in
	 * the {@link TreeMap}, the comparator should throw a {@link NullPointerException} when the first argument is
	 * <code>null</code>.
	 */
	private Comparator<K> comparator;

	/**
	 * The root {@link Node} of the {@link TreeMap}.
	 */
	private Node<K, V> root;

	/**
	 * The number of entries in the {@link HashMap}.
	 */
	private int size;

	/**
	 * Cached {@link TreeMapValueCollection} that can be returned from the {@link TreeMap#values()} method.
	 */
	private TreeMapValueCollection cacheValueCollection;

	/**
	 * Cached {@link TreeMapKeySet} that can be returned from the {@link TreeMap#keySet()} method.
	 */
	private TreeMapKeySet cacheKeySet;

	/**
	 * Cached {@link TreeMapEntrySet} that can be returned from the {@link TreeMap#entrySet()} method.
	 */
	private TreeMapEntrySet cacheEntrySet;

	/**
	 * Creates a new {@link TreeMap} using the provided <code>comparator</code>.
	 *
	 * @param comparator The comparator used when comparing the keys in the {@link TreeMap}. If <code>null</code> keys
	 *                   are not allowed in the {@link TreeMap}, the comparator should throw a {@link
	 *                   NullPointerException} when the first argument is <code>null</code>.
	 */
	public TreeMap(Comparator<K> comparator)
	{
		this.comparator = comparator;
	}

	/**
	 * Creates a new {@link TreeMap} using the provided <code>comparator</code>. The map is then filled with the
	 * entries from the provided <code>map</code>.
	 *
	 * @param comparator The comparator used when comparing the keys in the {@link TreeMap}. If <code>null</code> keys
	 *                   are not allowed in the {@link TreeMap}, the comparator should throw a {@link
	 *                   NullPointerException} when the first argument is <code>null</code>.
	 * @param map        The map from where the entries are taken and inserted into <code>this</code>.
	 */
	public TreeMap(Comparator<K> comparator, Map<? extends K, ? extends V> map)
	{
		this(comparator);
		putAll(map);
	}

	/**
	 * Represents a key-value node in the {@link TreeMap}.
	 *
	 * @param <K> The key type.
	 * @param <V> The value type.
	 */
	public static class Node<K, V> implements Map.Entry<K, V>
	{

		/**
		 * The key of the {@link Node}. This value is used when storing the {@link Node} in the {@link TreeMap}.
		 */
		private final K key;

		/**
		 * The value of the {@link Node}.
		 */
		private V value;

		/**
		 * The left (smaller) child {@link Node}.
		 */
		private Node<K, V> left;

		/**
		 * The right (greater) child {@link Node}.
		 */
		private Node<K, V> right;

		/**
		 * The parent of the {@link Node}.
		 */
		private Node<K, V> parent;

		/**
		 * Creates a new key-value {@link Node}.
		 *
		 * @param key   The key of the {@link Node}.
		 * @param value The value of the {@link Node}.
		 */
		public Node(K key, V value)
		{
			this(key, value, null);
		}

		/**
		 * Creates a new key-value {@link Node}.
		 *
		 * @param key    The key of the {@link Node}.
		 * @param value  The value of the {@link Node}.
		 * @param parent The parent of the {@link Node}.
		 */
		public Node(K key, V value, Node<K, V> parent)
		{
			this.key = key;
			this.value = value;
			this.parent = parent;
		}

		/**
		 * Returns the key of the {@link Node}.
		 *
		 * @return The key of the {@link Node}.
		 */
		public K getKey()
		{
			return this.key;
		}

		/**
		 * Returns the value of the {@link Node}.
		 *
		 * @return The value of the {@link Node}.
		 */
		public V getValue()
		{
			return this.value;
		}

		/**
		 * Sets the value of the {@link Node}.
		 *
		 * @param value The new value of the {@link Node}.
		 *
		 * @return The old value.
		 */
		public V setValue(V value)
		{
			V before = this.value;
			this.value = value;
			return before;
		}

		@Override public boolean equals(Object o)
		{
			if (this == o) return true;
			if (!(o instanceof Node)) return false;
			Node<?, ?> node = (Node<?, ?>) o;
			return Objects.equals(key, node.key) &&
				   Objects.equals(value, node.value);
		}

		@Override public int hashCode()
		{
			return Objects.hash(key, value);
		}
	}

	/**
	 * Abstract iterator, allows for iteration through the nodes in the {@link TreeMap} using the
	 * {@link TreeMapIterator#nextNode()} method.
	 *
	 * @param <T> The type argument for the iterator to create.
	 */
	abstract private class TreeMapIterator<T> implements Iterator<T>
	{

		/**
		 * The next node to be returned. <code>next</code> is <code>null</code> if there are no more nodes to return.
		 */
		private Node<K, V> next;

		/**
		 * The previously returned node.
		 */
		private Node<K, V> previous;

		/**
		 * Creates a new {@link TreeMapIterator}.
		 */
		TreeMapIterator()
		{
			next = minimum(root);
		}

		/**
		 * Returns {@code true} if the iteration has more elements. (In other words, returns {@code true} if {@link
		 * #next} would return an element rather than throwing an exception.)
		 *
		 * @return {@code true} if the iteration has more elements
		 */
		@Override public boolean hasNext()
		{
			return next != null;
		}

		/**
		 * Returns the next node in the iterator.
		 *
		 * @return The next node in the iterator.
		 */
		protected Node<K, V> nextNode()
		{
			if (next == null)
				throw new NoSuchElementException();

			Node<K, V> result = next;

			if (next.right != null) {
				next = next.right;
				while (next.left != null)
					next = next.left;
				previous = result;
				return result;
			}

			while (true) {

				if (next.parent == null) {
					next = null;
					previous = result;
					return result;
				}

				if (next.parent.left == next) {
					next = next.parent;
					previous = result;
					return result;
				}
				next = next.parent;
			}
		}

		/**
		 * Removes from the underlying collection the last element returned by this iterator. This method can be called
		 * only once per call to {@link #next}.  The behavior of an iterator is unspecified if the underlying
		 * collection
		 * is modified while the iteration is in progress in any way other than by calling this method.
		 *
		 * @throws IllegalStateException if the {@code next} method has not yet been called, or the {@code remove}
		 *                               method has already been called after the last call to the {@code next} method
		 */
		@Override public void remove()
		{
			if (previous == null)
				throw new IllegalStateException();

			removeNode(previous);
			previous = null;
		}
	}

	/**
	 * Iterator implementation extending from {@link TreeMapIterator} allowing for easy iteration of the nodes
	 * in the {@link TreeMap}.
	 */
	private final class TreeMapNodeIterator extends TreeMapIterator<Node<K, V>>
	{

		/**
		 * Returns the next node in the iteration.
		 *
		 * @return the next node in the iteration
		 * @throws NoSuchElementException if the iteration has no more nodes
		 */
		@Override public Node<K, V> next()
		{
			return nextNode();
		}
	}

	/**
	 * Returns the number of key-value mappings in this map.
	 *
	 * @return the number of key-value mappings in this map
	 */
	@Override public int size()
	{
		return size;
	}

	/**
	 * Returns <tt>true</tt> if this map contains no key-value mappings.
	 *
	 * @return <tt>true</tt> if this map contains no key-value mappings
	 */
	@Override public boolean isEmpty()
	{
		return size == 0;
	}

	/**
	 * Returns <tt>true</tt> if this map contains a mapping for the specified key.
	 *
	 * @param key The key whose presence in this map is to be tested.
	 *
	 * @return <tt>true</tt> if this map contains a mapping for the specified key.
	 * @throws ClassCastException if the provided key is of an inappropriate type for this map
	 */
	@Override public boolean containsKey(Object key)
	{
		try {
			return getNode((K) key, root) != null;
		} catch (ClassCastException e) {
			return false;
		}
	}

	/**
	 * Returns <tt>true</tt> if this map maps one or more keys to the specified value.
	 *
	 * @param value The value whose presence in this map is to be tested.
	 *
	 * @return <tt>true</tt> if this map maps one or more keys to the specified value.
	 */
	@Override public boolean containsValue(Object value)
	{
		try {
			return searchNode(value, root) != null;
		} catch (ClassCastException e) {
			return false;
		}
	}

	/**
	 * Returns the value to which the specified key is mapped, or {@code null} if this map contains no mapping for the
	 * key.
	 * <p>
	 * <p>More formally, if this map contains a mapping from a key {@code k} to a value {@code v} such that {@code
	 * (key==null ? k==null : key.equals(k))}, then this method returns {@code v}; otherwise it returns {@code null}.
	 * (There can be at most one such mapping.)
	 * <p>
	 * <p>A return value of {@code null} does not <i>necessarily</i> indicate that the map contains no mapping for the
	 * key; it's also possible that the map explicitly maps the key to {@code null}. The {@link #containsKey
	 * containsKey} operation may be used to distinguish these two cases.
	 *
	 * @param key the key whose associated value is to be returned
	 *
	 * @see #put(Object, Object)
	 */
	@Override public V get(Object key)
	{
		Node<K, V> node = getNode((K) key, root);

		return node == null ? null : node.value;
	}

	/**
	 * Associates the specified value with the specified key in this map. If the map previously contained a mapping for
	 * the key, the old value is replaced.
	 *
	 * @param key   key with which the specified value is to be associated
	 * @param value value to be associated with the specified key
	 *
	 * @return the previous value associated with <tt>key</tt>, or <tt>null</tt> if there was no mapping for
	 * <tt>key</tt>. (A <tt>null</tt> return can also indicate that the map previously associated <tt>null</tt> with
	 * <tt>key</tt>.)
	 * @throws ClassCastException if the provided key is of an inappropriate type for this map
	 */
	@Override public V put(K key, V value)
	{
		return putNode(key, value);
	}

	/**
	 * Removes the mapping for the specified key from this map if present.
	 *
	 * @param key key whose mapping is to be removed from the map
	 *
	 * @return the previous value associated with <tt>key</tt>, or <tt>null</tt> if there was no mapping for
	 * <tt>key</tt>. (A <tt>null</tt> return can also indicate that the map previously associated <tt>null</tt> with
	 * <tt>key</tt>.)
	 * @throws ClassCastException if the provided key is of an inappropriate type for this map
	 */
	@Override public V remove(Object key)
	{
		Node<K, V> removed = removeNode(getNode((K) key, root));

		return removed == null ? null : removed.value;
	}

	/**
	 * Removed the provided <code>node</code> from the {@link TreeMap}.
	 *
	 * @param node The node to remove  the {@link TreeMap}.
	 *
	 * @return The node that was removed. Returns <code>null</code> if no node was removed.
	 */
	private Node<K, V> removeNode(Node<K, V> node)
	{

		if (node == null)
			return null;

		if (node.left == null && node.right == null) {
			replaceNode(node, null);
			size--;
			return node;
		}

		// Effective XOR because of the above &&
		if (node.left == null || node.right == null) {

			if (node.left != null) {
				replaceNode(node, node.left);
				size--;
				return node;
			}

			if (node.right != null) {
				replaceNode(node, node.right);
				size--;
				return node;
			}
		}

		Node<K, V> min = minimum(node.right);
		min.left = node.left;
		replaceNode(node, min);
		size--;
		return node;
	}

	/**
	 * Finds the smallest node in the tree headed by the provided <code>node</code>.
	 *
	 * @param head The head of the tree in which to find the smallest node.
	 *
	 * @return The smallest node in the tree. Returns <code>null</code> if no node could be found.
	 */
	private Node<K, V> minimum(Node<K, V> head)
	{
		while (head != null) {
			if (head.left == null)
				return head;
			head = head.left;
		}

		return null;
	}

	/**
	 * Replaces the provided node <code>target</code> with the provided node <code>replacement</code>.
	 *
	 * @param target      The node to replace.
	 * @param replacement The replacement for the node to remove.
	 */
	private void replaceNode(Node<K, V> target, Node<K, V> replacement)
	{
		Node<K, V> parent = target.parent;

		if (parent == null) {
			root = replacement;
			if (replacement != null)
				replacement.parent = null;
		} else if (parent.left == target) {
			parent.left = replacement;
			if (replacement != null)
				replacement.parent = parent;
		} else if (parent.right == target) {
			parent.right = replacement;
			if (replacement != null)
				replacement.parent = parent;
		}
	}

	/**
	 * Copies all of the mappings from the specified map to this map.  The effect of this call is
	 * equivalent to that of calling {@link #put(Object, Object) put(k, v)} on this map once for each mapping from key
	 * <tt>k</tt> to value <tt>v</tt> in the specified map.  The behavior of this operation is undefined if the
	 * specified map is modified while the operation is in progress.
	 *
	 * @param m mappings to be stored in this map.
	 */
	@Override public void putAll(Map<? extends K, ? extends V> m)
	{
		if (m != null) {
			for (Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
				put(entry.getKey(), entry.getValue());
			}
		}
	}

	/**
	 * Removes all of the mappings from this map.
	 */
	@Override public void clear()
	{
		root = null;
		size = 0;
	}

	/**
	 * Returns a {@link Collection} view of the values contained in this map. The collection is backed by the map, so
	 * changes to the map are reflected in the collection, and vice-versa.  If the map is modified while an iteration
	 * over the collection is in progress (except through the iterator's own <tt>remove</tt> operation), the results of
	 * the iteration are undefined.  The collection supports element removal, which removes the corresponding mapping
	 * from the map, via the <tt>Iterator.remove</tt>, <tt>Collection.remove</tt>, <tt>removeAll</tt>,
	 * <tt>retainAll</tt> and <tt>clear</tt> operations.  It does not support the <tt>add</tt> or <tt>addAll</tt>
	 * operations.
	 *
	 * @return a collection view of the values contained in this map
	 */
	@Override public Collection<V> values()
	{
		if (cacheValueCollection == null)
			cacheValueCollection = new TreeMapValueCollection();

		return cacheValueCollection;
	}

	/**
	 * Collection backed by the {@link TreeMap}. Changed made to the {@link TreeMap} are reflected in the
	 * {@link TreeMapValueCollection} and vice versa. The same instance of {@link TreeMapValueCollection} should be
	 * returned on multiple calls to the {@link TreeMap#values()} method.
	 */
	private class TreeMapValueCollection implements Collection<V>
	{

		/**
		 * Returns the number of values in the {@link TreeMap}.
		 *
		 * @return The number of values in the {@link TreeMap}.
		 */
		@Override public int size()
		{
			return size;
		}

		/**
		 * Returns <code>true</code> if the {@link TreeMap} contains no values.
		 *
		 * @return <code>true</code> if the {@link TreeMap} contains no values.
		 */
		@Override public boolean isEmpty()
		{
			return size == 0;
		}

		/**
		 * Returns <code>true</code> if the {@link TreeMap} contains the specified value.
		 *
		 * @param o value whose presence in the {@link TreeMap} is to be tested.
		 *
		 * @return <code>true</code> if the {@link TreeMap} contains the specified value.
		 */
		@Override public boolean contains(Object o)
		{
			return searchNode(o, root) != null;
		}

		/**
		 * Returns an iterator over the values in the {@link TreeMap}. The values are returned according to
		 * ascending order of their keys.
		 *
		 * @return an <tt>Iterator</tt> over the values in the {@link TreeMap}.
		 */
		@Override public Iterator<V> iterator()
		{
			return new HashMapValueIterator();
		}

		/**
		 * Iterator implementation extending from {@link TreeMapIterator} allowing for easy iteration of the values
		 * in the {@link TreeMap}.
		 */
		private class HashMapValueIterator extends TreeMapIterator<V>
		{

			/**
			 * Returns the next value in the iteration.
			 *
			 * @return the next value in the iteration
			 * @throws NoSuchElementException if the iteration has no more values
			 */
			@Override public V next()
			{
				return super.nextNode().value;
			}
		}

		/**
		 * Returns an array containing all of the values in the {@link TreeMap}.
		 *
		 * @return an array containing all of the values in the {@link TreeMap}
		 */
		@Override public Object[] toArray()
		{
			Object[]             result   = new Object[size];
			Iterator<Node<K, V>> iterator = new TreeMapNodeIterator();
			int                  index    = 0;
			while (iterator.hasNext()) {
				result[index++] = iterator.next().value;
			}

			return result;
		}

		/**
		 * Returns an array containing all of the values in the {@link TreeMap}; the runtime type of the returned
		 * array is that of the specified array. If the values fits in the specified array, it is returned therein.
		 * Otherwise, a new array is allocated with the runtime type of the specified array and the size of the
		 * {@link TreeMap}.
		 *
		 * @param a the array into which the values of the {@link TreeMap} are to be stored, if it is big enough;
		 *          otherwise, a new array of the same runtime type is allocated for this purpose.
		 *
		 * @return an array containing all of the values in the {@link TreeMap}
		 * @throws ArrayStoreException if the runtime type of the specified array is not a supertype of the runtime
		 *                             type of every value in the {@link TreeMap}
		 */
		@Override public <T> T[] toArray(T[] a)
		{
			int size = size();
			T[] r = a.length >= size && a != null ? a : (T[]) java.lang.reflect.Array.newInstance(
					a.getClass().getComponentType(),
					size
			);

			Iterator<Node<K, V>> iterator = new TreeMapNodeIterator();
			int                  index    = 0;
			while (iterator.hasNext()) {
				r[index++] = (T) iterator.next().value;
			}

			return r;
		}

		/**
		 * Removes a single instance of the specified value from the {@link TreeMap}, if it is present.
		 *
		 * @param o value to be removed from the {@link TreeMap}, if present
		 *
		 * @return <tt>true</tt> if a value was removed as a result of this call
		 */
		@Override public boolean remove(Object o)
		{
			Node<K, V> removed = removeNode(searchNode(o, root));

			return removed != null;
		}

		/**
		 * Returns <tt>true</tt> if the {@link TreeMap} contains all of the values in the specified collection.
		 *
		 * @param c collection to be checked for containment in the {@link TreeMap}
		 *
		 * @return <tt>true</tt> if the {@link TreeMap} contains all of the elements in the specified {@link TreeMap}.
		 * @see #contains(Object)
		 */
		@Override public boolean containsAll(Collection<?> c)
		{
			if (c == null || c.isEmpty())
				return false;

			for (Object o : c)
				if (!TreeMap.this.containsValue(o))
					return false;

			return true;
		}

		/**
		 * Removes all of the {@link TreeMap}'s values that are also contained in the specified collection. After this
		 * call returns, the {@link TreeMap} will contain no values in common with the specified collection.
		 *
		 * @param c collection containing values to be removed from the {@link TreeMap}.
		 *
		 * @return <tt>true</tt> if the {@link TreeMap} changed as a result of the call.
		 * @see #remove(Object)
		 * @see #contains(Object)
		 */
		@Override public boolean removeAll(Collection<?> c)
		{
			if (c == null || c.isEmpty())
				return false;

			boolean changed = false;
			for (Object o : c) {
				Node<K, V> node = searchNode(o, root);
				if (node != null) {
					removeNode(node);
					changed = true;
				}
			}

			return changed;
		}

		/**
		 * Retains only the values in the {@link TreeMap} that are contained in the specified collection.  In other
		 * words: removes from the {@link TreeMap} all of its values that are not contained in the specified
		 * collection.
		 *
		 * @param c collection containing values to be retained in the {@link TreeMap}
		 *
		 * @return <tt>true</tt> if the {@link TreeMap} changed as a result of the call
		 * @see #remove(Object)
		 * @see #contains(Object)
		 */
		@Override public boolean retainAll(Collection<?> c)
		{
			boolean             changed  = false;
			TreeMapNodeIterator iterator = new TreeMapNodeIterator();
			while (iterator.hasNext()) {
				if (!c.contains(iterator.next().value)) {
					iterator.remove();
					changed = true;
				}
			}

			return changed;
		}

		/**
		 * Removes all of the elements from the {@link TreeMap}.
		 */
		@Override public void clear()
		{
			TreeMap.this.clear();
		}

		/**
		 * Unsupported operation.
		 *
		 * @throws UnsupportedOperationException
		 */
		@Override public boolean add(V v)
		{
			throw new UnsupportedOperationException();
		}

		/**
		 * Unsupported operation.
		 *
		 * @throws UnsupportedOperationException
		 */
		@Override public boolean addAll(Collection<? extends V> c)
		{
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * Returns a {@link Set} view of the keys contained in this map. The set is backed by the map, so changes to the
	 * map are reflected in the set, and vice-versa.
	 *
	 * @return a set view of the keys contained in this map
	 */
	@Override public Set<K> keySet()
	{
		if (cacheKeySet == null)
			cacheKeySet = new TreeMapKeySet();

		return cacheKeySet;
	}

	/**
	 * Set backed by the {@link TreeMap}. Changed made to the {@link TreeMap} are reflected in the
	 * {@link TreeMapKeySet} and vice versa. The same instance of {@link TreeMapKeySet} should be
	 * returned on multiple calls to the {@link TreeMap#keySet()} method.
	 */
	private final class TreeMapKeySet implements Set<K>
	{

		/**
		 * Returns the number of keys in the {@link TreeMap}.
		 *
		 * @return the number of keys in the {@link TreeMap}.
		 */
		@Override public int size()
		{
			return size;
		}

		/**
		 * Returns <tt>true</tt> if the {@link TreeMap} contains no keys.
		 *
		 * @return <tt>true</tt> if the {@link TreeMap} contains no keys.
		 */
		@Override public boolean isEmpty()
		{
			return size == 0;
		}

		/**
		 * Returns <tt>true</tt> if the {@link TreeMap} contains the specified key.
		 *
		 * @param o key whose presence in the {@link TreeMap} is to be tested
		 *
		 * @return <tt>true</tt> if the {@link TreeMap} contains the specified key
		 * @throws ClassCastException if the type of the specified key is incompatible with the {@link TreeMap}
		 */
		@Override public boolean contains(Object o)
		{
			return containsKey(o);
		}

		/**
		 * Returns an iterator over the keys in the {@link TreeMap}.  The keys are returned in ascending order by key.
		 *
		 * @return an iterator over the keys in the {@link TreeMap}
		 */
		@Override public Iterator<K> iterator()
		{
			return new TreeMapKeyIterator();
		}

		/**
		 * Iterator implementation extending from {@link TreeMapIterator} allowing for easy iteration of the keys
		 * in the {@link TreeMap}.
		 */
		private class TreeMapKeyIterator extends TreeMapIterator<K>
		{

			/**
			 * Returns the next key in the iteration.
			 *
			 * @return the next key in the iteration
			 * @throws NoSuchElementException if the iteration has no more keys
			 */
			@Override public K next()
			{
				return nextNode().key;
			}
		}

		/**
		 * Returns an array containing all of the keys in the {@link TreeMap}. If the {@link TreeMap} makes any guarantees as to what
		 * order its keys are returned by its iterator, this method must return the keys in the same order.
		 *
		 * @return an array containing all the keys in the {@link TreeMap}
		 */
		@Override public Object[] toArray()
		{
			Object[]             result   = new Object[size];
			Iterator<Node<K, V>> iterator = new TreeMapNodeIterator();
			int                  index    = 0;
			while (iterator.hasNext()) {
				result[index++] = iterator.next().key;
			}

			return result;
		}

		/**
		 * Returns an array containing all of the keys in the {@link TreeMap}; the runtime type of the returned array is that
		 * of the specified array. If the set fits in the specified array, it is returned therein. Otherwise, a new
		 * array is allocated with the runtime type of the specified array and the size of the {@link TreeMap}.
		 *
		 * @param a the array into which the keys of the {@link TreeMap} are to be stored, if it is big enough; otherwise, a
		 *          new array of the same runtime type is allocated for this purpose.
		 *
		 * @return an array containing all the keys in the {@link TreeMap}
		 * @throws ArrayStoreException if the runtime type of the specified array is not a supertype of the runtime
		 *                             type of every key in the {@link TreeMap}
		 */
		@Override public <T> T[] toArray(T[] a)
		{
			T[] r = a.length >= size && a != null ? a : (T[]) java.lang.reflect.Array.newInstance(
					a.getClass().getComponentType(),
					size
			);

			Iterator<Node<K, V>> iterator = new TreeMapNodeIterator();
			int                  index    = 0;
			while (iterator.hasNext()) {
				r[index++] = (T) iterator.next().key;
			}

			return r;
		}

		/**
		 * Removes the specified key from the {@link TreeMap} if it is present More formally, removes an key <tt>e</tt>
		 * such that <tt>(o==null ? e==null : o.equals(e))</tt>, if the {@link TreeMap} contains such an key.  Returns
		 * <tt>true</tt> if the {@link TreeMap} contained the key (or equivalently, if the {@link TreeMap} changed as a result of the
		 * call).  (the {@link TreeMap} will not contain the key once the call returns.)
		 *
		 * @param o key to be removed from the {@link TreeMap}, if present
		 *
		 * @return <tt>true</tt> if the {@link TreeMap} contained the specified key
		 * @throws ClassCastException if the type of the specified key is incompatible with the {@link TreeMap}
		 */
		@Override public boolean remove(Object o)
		{
			return TreeMap.this.remove(o) != null;
		}

		/**
		 * Returns <tt>true</tt> if the {@link TreeMap} contains all of the keys of the specified collection.  If the
		 * specified collection is also a set, this method returns <tt>true</tt> if it is a <i>subset</i> of the {@link TreeMap}.
		 *
		 * @param c collection to be checked for containment in the {@link TreeMap}
		 *
		 * @return <tt>true</tt> if the {@link TreeMap} contains all of the keys of the specified collection
		 * @throws ClassCastException if the type of the specified key is incompatible with the {@link TreeMap}
		 * @see #contains(Object)
		 */
		@Override public boolean containsAll(Collection<?> c)
		{
			if (c == null || c.isEmpty())
				return true;

			for (Object o : c)
				if (!contains(o))
					return false;

			return true;
		}

		/**
		 * Retains only the keys in the {@link TreeMap} that are contained in the specified collection. In other words,
		 * removes from the {@link TreeMap} all of its keys that are not contained in the specified collection .
		 * If the specified collection is also a set, this operation effectively modifies the {@link TreeMap} so that its value
		 * is the <i>intersection</i> of the two sets.
		 *
		 * @param c collection containing keys to be retained in the {@link TreeMap}
		 *
		 * @return <tt>true</tt> if the {@link TreeMap} changed as a result of the call
		 * @throws ClassCastException if the type of the specified key is incompatible with the {@link TreeMap}
		 * @see #remove(Object)
		 */
		@Override public boolean retainAll(Collection<?> c)
		{
			if (c == null || c.isEmpty()) {
				if (size != 0) {
					TreeMap.this.clear();
					return true;
				}

				return false;
			}

			boolean             changed  = false;
			TreeMapNodeIterator iterator = new TreeMapNodeIterator();
			while (iterator.hasNext()) {
				Node<K, V> node = iterator.next();
				if (!c.contains(node.key)) {
					removeNode(node);
					changed = true;
				}
			}

			return changed;
		}

		/**
		 * Removes from the {@link TreeMap} all of its keys that are contained in the specified collection (optional
		 * operation).  If the specified collection is also a set, this operation effectively modifies the {@link TreeMap} so that
		 * its value is the <i>asymmetric set difference</i> of the two sets.
		 *
		 * @param c collection containing keys to be removed from the {@link TreeMap}
		 *
		 * @return <tt>true</tt> if the {@link TreeMap} changed as a result of the call
		 * @throws ClassCastException if the type of the specified key is incompatible with the {@link TreeMap}
		 * @see #remove(Object)
		 * @see #contains(Object)
		 */
		@Override public boolean removeAll(Collection<?> c)
		{
			if (c == null || c.isEmpty()) {
				return false;
			}

			boolean             changed  = false;
			TreeMapNodeIterator iterator = new TreeMapNodeIterator();
			while (iterator.hasNext()) {
				Node<K, V> node = iterator.next();
				if (c.contains(node.key)) {
					removeNode(node);
					changed = true;
				}
			}

			return changed;
		}

		/**
		 * Removes all of the keys from the {@link TreeMap}.
		 */
		@Override public void clear()
		{
			TreeMap.this.clear();
		}

		/**
		 * Unsupported operation.
		 *
		 * @throws UnsupportedOperationException
		 */
		@Override public boolean add(K k)
		{
			throw new UnsupportedOperationException();
		}

		/**
		 * Unsupported operation.
		 *
		 * @throws UnsupportedOperationException
		 */
		@Override public boolean addAll(Collection<? extends K> c)
		{
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * Returns a {@link Set} view of the mappings contained in this map. The set is backed by the map, so changes to
	 * the map are reflected in the set, and vice-versa.
	 *
	 * @return a set view of the mappings contained in this map
	 */
	@Override public Set<Entry<K, V>> entrySet()
	{
		if (cacheEntrySet == null)
			cacheEntrySet = new TreeMapEntrySet();

		return cacheEntrySet;
	}

	/**
	 * Set backed by the {@link TreeMap}. Changed made to the {@link TreeMap} are reflected in the
	 * {@link TreeMapEntrySet} and vice versa. The same instance of {@link TreeMapEntrySet} should be
	 * returned on multiple calls to the {@link TreeMap#entrySet()} method.
	 */
	private final class TreeMapEntrySet implements Set<Map.Entry<K, V>>
	{

		/**
		 * Returns the number of elements in this set.
		 *
		 * @return the number of elements in this set.
		 */
		@Override public int size()
		{
			return size;
		}

		/**
		 * Returns <tt>true</tt> if this set contains no elements.
		 *
		 * @return <tt>true</tt> if this set contains no elements
		 */
		@Override public boolean isEmpty()
		{
			return size == 0;
		}

		/**
		 * Returns <tt>true</tt> if this set contains the specified element.
		 *
		 * @param o element whose presence in this set is to be tested
		 *
		 * @return <tt>true</tt> if this set contains the specified element
		 */
		@Override public boolean contains(Object o)
		{
			try {
				Entry<K, V> entry = (Entry<K, V>) o;
				return getNode(entry.getKey(), entry.getValue(), root) != null;
			} catch (ClassCastException e) {
				return false;
			}
		}

		/**
		 * Returns an iterator over the elements in this set. The elements are returned in ascending order by key.
		 *
		 * @return an iterator over the elements in this set
		 */
		@Override public Iterator<Entry<K, V>> iterator()
		{
			return new TreeMapEntryIterator();
		}

		/**
		 * Iterator implementation extending from {@link TreeMapIterator} allowing for easy iteration of the entries
		 * in the {@link TreeMap}.
		 */
		private final class TreeMapEntryIterator extends TreeMapIterator<Entry<K, V>>
		{

			/**
			 * Returns the next entry in the iteration.
			 *
			 * @return the next entry in the iteration
			 * @throws NoSuchElementException if the iteration has no more entries
			 */
			@Override public Entry<K, V> next()
			{
				return nextNode();
			}
		}

		/**
		 * Returns an array containing all of the entries in the {@link TreeMap}. The resulting array can be modified without
		 * affecting the set.
		 *
		 * @return the array containing all the entries in the {@link TreeMap}
		 */
		@Override public Object[] toArray()
		{
			int                  counter  = 0;
			Object[]             result   = new Object[size];
			TreeMapEntryIterator iterator = new TreeMapEntryIterator();
			while (iterator.hasNext())
				result[counter++] = iterator.next();
			return result;
		}

		/**
		 * Returns an array containing all of the entries in the {@link TreeMap}; the runtime type of the returned array is that
		 * of the specified array. If the set fits in the specified array, it is returned therein. Otherwise, a new
		 * array is allocated with the runtime type of the specified array and the size of the {@link TreeMap}.
		 *
		 * @param a the array into which the entries of the {@link TreeMap} are to be stored, if it is big enough; otherwise, a
		 *          new array of the same runtime type is allocated for this purpose.
		 *
		 * @return an array containing all the entries in the {@link TreeMap}
		 * @throws ArrayStoreException if the runtime type of the specified array is not a supertype of the runtime
		 *                             type of every entry in the {@link TreeMap}
		 */
		@Override public <T> T[] toArray(T[] a)
		{
			T[] r = a.length >= size && a != null ? a : (T[]) java.lang.reflect.Array.newInstance(
					a.getClass().getComponentType(),
					size
			);

			TreeMapEntryIterator iterator = new TreeMapEntryIterator();
			int                  index    = 0;
			while (iterator.hasNext())
				r[index++] = (T) iterator.next();

			return r;
		}

		/**
		 * Adds the provided entry to the {@link TreeMapEntrySet} when it does not already exist in the set. The entry
		 * is only added when a {@link Node} with the an equal key and value doesn't exist in the set. Null values
		 * cannot be allowed.
		 *
		 * @param entry element to be added to the {@link TreeMap}
		 *
		 * @return <tt>true</tt> if the {@link TreeMap} did not already contain the specified element
		 */
		@Override public boolean add(Entry<K, V> entry)
		{
			if (contains(entry))
				return false;

			putNode(entry.getKey(), entry.getValue());
			return true;
		}

		/**
		 * Removes the specified element from the {@link TreeMap} if it is present. More formally, removes an element <tt>e</tt>
		 * such that <tt>(o==null ? e==null : o.equals(e))</tt>, if the {@link TreeMap} contains such an element.  Returns
		 * <tt>true</tt> if the {@link TreeMap} contained the element (or equivalently, if the {@link TreeMap} changed as a result of the
		 * call).
		 *
		 * @param o object to be removed from the {@link TreeMap}, if present
		 *
		 * @return <tt>true</tt> if the {@link TreeMap} contained the specified element
		 */
		@Override public boolean remove(Object o)
		{
			try {
				Entry<K, V> entry = (Entry<K, V>) o;
				Node<K, V>  node  = getNode(entry.getKey(), entry.getValue(), root);
				if (node != null) {
					removeNode(node);
					return true;
				}

				return false;
			} catch (ClassCastException e) {
				return false;
			}
		}

		/**
		 * Returns <tt>true</tt> if the {@link TreeMap} contains all of the entries of the specified collection.  If the
		 * specified collection is also a set, this method returns <tt>true</tt> if it is a <i>subset</i> of the {@link TreeMap}.
		 *
		 * @param c collection to be checked for containment in the {@link TreeMap}
		 *
		 * @return <tt>true</tt> if the {@link TreeMap} contains all of the entries of the specified collection
		 * @see #contains(Object)
		 */
		@Override public boolean containsAll(Collection<?> c)
		{
			if (c == null || c.isEmpty())
				return true;

			for (Object o : c)
				if (!contains(o))
					return false;

			return true;
		}

		/**
		 * Adds all of the entries in the specified collection to the {@link TreeMap} if they're not already present.  If the
		 * specified collection is also a set, the <tt>addAll</tt> operation effectively modifies the {@link TreeMap} so that its
		 * value is the <i>union</i> of the two sets.  The behavior of this operation is undefined if the specified
		 * collection is modified while the operation is in progress.
		 *
		 * @param c collection containing entries to be added to the {@link TreeMap}
		 *
		 * @return <tt>true</tt> if the {@link TreeMap} changed as a result of the call
		 * @see #add(Object)
		 */
		@Override public boolean addAll(Collection<? extends Entry<K, V>> c)
		{
			if (c == null || c.isEmpty())
				return false;

			boolean changed = false;
			for (Entry<K, V> entry : c) {
				if (!contains(entry)) {
					putNode(entry.getKey(), entry.getValue());
					changed = true;
				}
			}

			return changed;
		}

		/**
		 * Retains only the entries in the {@link TreeMap} that are contained in the specified collection. In other words,
		 * removes
		 * from the {@link TreeMap} all of its entries that are not contained in the specified collection . If the specified
		 * collection is also a set, this operation effectively modifies the {@link TreeMap} so that its value is the
		 * <i>intersection</i> of the two sets.
		 *
		 * @param c collection containing entries to be retained in the {@link TreeMap}
		 *
		 * @return <tt>true</tt> if the {@link TreeMap} changed as a result of the call
		 * @see #remove(Object)
		 */
		@Override public boolean retainAll(Collection<?> c)
		{
			if (c == null || c.isEmpty()) {
				if (size == 0)
					return false;

				TreeMap.this.clear();
				return true;
			}

			boolean              changed  = false;
			TreeMapEntryIterator iterator = new TreeMapEntryIterator();
			while (iterator.hasNext()) {
				Node<K, V> node = iterator.nextNode();
				if (!c.contains(node)) {
					removeNode(node);
					changed = true;
				}
			}

			return changed;
		}

		/**
		 * Removes from the {@link TreeMap} all of its entries that are contained in the specified collection.  If the specified
		 * collection is also a set, this operation effectively modifies the {@link TreeMap} so that its value is the
		 * <i>asymmetric
		 * set difference</i> of the two sets.
		 *
		 * @param c collection containing entries to be removed from the {@link TreeMap}
		 *
		 * @return <tt>true</tt> if the {@link TreeMap} changed as a result of the call
		 * @see #remove(Object)
		 * @see #contains(Object)
		 */
		@Override public boolean removeAll(Collection<?> c)
		{
			if (c == null || c.isEmpty()) {
				return false;
			}

			boolean changed = false;
			for (Object object : c) {
				Entry<K, V> entry = (Entry<K, V>) object;
				Node<K, V>  node  = getNode(entry.getKey(), entry.getValue(), root);
				if (node != null) {
					removeNode(node);
					changed = true;
				}
			}

			return changed;
		}

		/**
		 * Removes all of the entries from the {@link TreeMap}.
		 */
		@Override public void clear()
		{
			TreeMap.this.clear();
		}
	}

	/**
	 * Inserts a node with the provided key and value into the {@link TreeMap}.
	 *
	 * @param key   The key of the node to insert.
	 * @param value The value of the node to insert.
	 *
	 * @return The value, if any, that was overwritten while inserting the node.
	 */
	private V putNode(K key, V value)
	{
		if (root == null) {
			root = new Node<>(key, value);
			size++;
			return null;
		}

		return putNode(key, value, root);
	}

	/**
	 * Recursive insert method.
	 *
	 * @param key   The key of the node to insert.
	 * @param value The value of the node to insert.
	 * @param node  The node currently being looked at by the recursive method.
	 *
	 * @return
	 * @see #putNode(Object, Object)
	 */
	private V putNode(K key, V value, Node<K, V> node)
	{
		if (key == null ? key == node.key : key.equals(node.key)) {
			V before = node.value;
			node.value = value;
			return before;
		}

		int compare = comparator.compare(key, node.key);

		if (compare < 0) {
			if (node.left == null) {
				node.left = new Node<>(key, value, node);
				size++;
				return null;
			}

			return putNode(key, value, node.left);
		}

		if (compare > 0) {
			if (node.right == null) {
				node.right = new Node<>(key, value, node);
				size++;
				return null;
			}

			return putNode(key, value, node.right);
		}

		return null;
	}

	/**
	 * Finds and returns the node with the matching provided key.
	 *
	 * @param key  The key of the node to find and return.
	 * @param node The node currently being considered by the recursive method.
	 *
	 * @return The node with the provided key. Returns <code>null</code> if no such node could be found.
	 */
	private Node<K, V> getNode(K key, Node<K, V> node)
	{
		if (node == null)
			return null;

		if (key == null ? key == node.key : key.equals(node.key)) {
			return node;
		}

		int compare = comparator.compare(key, node.key);

		if (compare < 0)
			return getNode(key, node.left);
		if (compare > 0)
			return getNode(key, node.right);

		return null;
	}

	/**
	 * Finds and returns the node with the matching provided key and value.
	 *
	 * @param key   The key of the node to find and return.
	 * @param value The value of the node to find and return.
	 * @param node  The node currently being considered by the recursive method.
	 *
	 * @return The node with the provided key. Returns <code>null</code> if no such node could be found.
	 */
	private Node<K, V> getNode(K key, V value, Node<K, V> node)
	{
		if (node == null)
			return null;

		if ((key == null ? node.key == null : key.equals(node.key)) &&
			(value == null ? node.value == null : value.equals(node.value))) {
			return node;
		}

		int compare = comparator.compare(key, node.key);

		if (compare < 0)
			return getNode(key, value, node.left);
		if (compare > 0)
			return getNode(key, value, node.right);

		return null;
	}

	/**
	 * Finds the returns the node with the provided value.
	 *
	 * @param value The value of the node to find.
	 * @param node  The node currently being considered by the recursive method.
	 *
	 * @return The node matching the provided value. Returns <code>null</code> if no such node could be found.
	 */
	private Node<K, V> searchNode(Object value, Node<K, V> node)
	{
		if (node == null)
			return null;

		if (value == null ? value == node.value : value.equals(node.value)) {
			return node;
		}

		Node<K, V> searchL = searchNode(value, node.left);
		if (searchL != null)
			return searchL;

		Node<K, V> searchR = searchNode(value, node.right);
		if (searchR != null)
			return searchR;

		return null;
	}
}
