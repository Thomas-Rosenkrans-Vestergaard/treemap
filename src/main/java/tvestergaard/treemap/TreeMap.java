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

	private Node<K, V>             root;
	private int                    size;
	private TreeMapValueCollection cacheValueCollection;
	private TreeMapKeySet          cacheKeySet;

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

		private Node<K, V> left;
		private Node<K, V> right;
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
		 * Returns the key in the key-value node.
		 *
		 * @return The key in the key-value node.
		 */
		public K getKey()
		{
			return this.key;
		}

		/**
		 * Returns the value in the key-value node.
		 *
		 * @return The value in the key-value node.
		 */
		public V getValue()
		{
			return this.value;
		}

		/**
		 * Sets the value in the key-value node.
		 *
		 * @param value The new value to set.
		 *
		 * @return The value just replaced.
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
	 * Returns the number of key-value mappings in this map.  If the map contains more than <tt>Integer.MAX_VALUE</tt>
	 * elements, returns <tt>Integer.MAX_VALUE</tt>.
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
	 * Returns <tt>true</tt> if this map contains a mapping for the specified key.  More formally, returns
	 * <tt>true</tt>
	 * if and only if this map contains a mapping for a key <tt>k</tt> such that <tt>(key==null ? k==null :
	 * key.equals(k))</tt>.  (There can be at most one such mapping.)
	 *
	 * @param key key whose presence in this map is to be tested
	 *
	 * @return <tt>true</tt> if this map contains a mapping for the specified key
	 * @throws ClassCastException if the provided key is of an inappropriate type for this map
	 */
	@Override public boolean containsKey(Object key)
	{
		Node<K, V> node = getNode((K) key, root);

		return node != null;
	}

	/**
	 * Returns <tt>true</tt> if this map maps one or more keys to the specified value.  More formally, returns
	 * <tt>true</tt> if and only if this map contains at least one mapping to a value <tt>v</tt> such that
	 * <tt>(value==null ? v==null : value.equals(v))</tt>.  This operation will probably require time linear in the map
	 * size for most implementations of the <tt>Map</tt> interface.
	 *
	 * @param value value whose presence in this map is to be tested
	 *
	 * @return <tt>true</tt> if this map maps one or more keys to the specified value
	 * @throws ClassCastException if the provided key is of an inappropriate type for this map
	 */
	@Override public boolean containsValue(Object value)
	{
		Node<K, V> node = searchNode(value, root);

		return node != null;
	}

	/**
	 * Returns the value to which the specified key is mapped, or {@code null} if this map contains no mapping for the
	 * key.
	 * <p>
	 * <p>More formally, if this map contains a mapping from a key {@code k} to a value {@code v} such that {@code
	 * (key==null ? k==null : key.equals(k))}, then this method returns {@code v}; otherwise it returns {@code null}.
	 * (There can be at most one such mapping.)
	 * <p>
	 * <p>If this map permits null values, then a return value of {@code null} does not <i>necessarily</i> indicate
	 * that
	 * the map contains no mapping for the key; it's also possible that the map explicitly maps the key to {@code
	 * null}.
	 * The {@link #containsKey containsKey} operation may be used to distinguish these two cases.
	 *
	 * @param key the key whose associated value is to be returned
	 *
	 * @return the value to which the specified key is mapped, or {@code null} if this map contains no mapping for the
	 * key
	 * @throws ClassCastException if the provided key is of an inappropriate type for this map
	 */
	@Override public V get(Object key)
	{
		Node<K, V> node = getNode((K) key, root);

		return node == null ? null : node.value;
	}

	/**
	 * Associates the specified value with the specified key in this map (optional operation).  If the map previously
	 * contained a mapping for the key, the old value is replaced by the specified value.  (A map <tt>m</tt> is said to
	 * contain a mapping for a key <tt>k</tt> if and only if {@link #containsKey(Object) m.containsKey(k)} would return
	 * <tt>true</tt>.)
	 *
	 * @param key   key with which the specified value is to be associated
	 * @param value value to be associated with the specified key
	 *
	 * @return the previous value associated with <tt>key</tt>, or <tt>null</tt> if there was no mapping for
	 * <tt>key</tt>. (A <tt>null</tt> return can also indicate that the map previously associated <tt>null</tt> with
	 * <tt>key</tt>, if the implementation supports <tt>null</tt> values.)
	 * @throws ClassCastException if the provided key is of an inappropriate type for this map
	 */
	@Override public V put(K key, V value)
	{
		return putNode(key, value);
	}

	/**
	 * Removes the mapping for a key from this map if it is present (optional operation).   More formally, if this map
	 * contains a mapping from key <tt>k</tt> to value <tt>v</tt> such that <code>(key==null ?  k==null :
	 * key.equals(k))</code>, that mapping is removed.  (The map can contain at most one such mapping.)
	 * <p>
	 * <p>Returns the value to which this map previously associated the key, or <tt>null</tt> if the map contained no
	 * mapping for the key.
	 * <p>
	 * <p>If this map permits null values, then a return value of <tt>null</tt> does not <i>necessarily</i> indicate
	 * that the map contained no mapping for the key; it's also possible that the map explicitly mapped the key to
	 * <tt>null</tt>.
	 * <p>
	 * <p>The map will not contain a mapping for the specified key once the call returns.
	 *
	 * @param key key whose mapping is to be removed from the map
	 *
	 * @return the previous value associated with <tt>key</tt>, or <tt>null</tt> if there was no mapping for
	 * <tt>key</tt>.
	 * @throws ClassCastException if the provided key is of an inappropriate type for this map
	 */
	@Override public V remove(Object key)
	{
		Node<K, V> removed = removeNode(getNode((K) key, root));

		return removed == null ? null : removed.value;
	}

	private Node<K, V> removeNode(Node<K, V> node)
	{

		if (node == null)
			return null;

		if (node.left == null && node.right == null) {
			replaceNode(node.parent, node, null);
			size--;
			return node;
		}

		if (node.left == null || node.right == null) {

			if (node.left != null) {
				replaceNode(node.parent, node, node.left);
				size--;
				return node;
			}

			if (node.right != null) {
				replaceNode(node.parent, node, node.right);
				size--;
				return node;
			}
		}

		Node<K, V> min = minimum(node.right);
		min.left = node.left;
		replaceNode(node.parent, node, min);
		size--;
		return node;
	}

	private Node<K, V> removeNode(K key, Node<K, V> node, Node<K, V> parent)
	{
		if (node == null)
			return null;

		if (key == null ? key == node.key : key.equals(node.key)) {

			if (node.left == null && node.right == null) {
				replaceNode(parent, node, null);
				size--;
				return node;
			}

			if (node.left == null || node.right == null) {

				if (node.left != null) {
					replaceNode(parent, node, node.left);
					size--;
					return node;
				}

				if (node.right != null) {
					replaceNode(parent, node, node.right);
					size--;
					return node;
				}
			}

			Node<K, V> min = minimum(node.right);
			min.left = node.left;
			replaceNode(parent, node, min);
			size--;
			return node;
		}

		int compare = comparator.compare(key, node.key);

		if (compare < 0)
			return removeNode(key, node.left, node);
		if (compare > 0)
			return removeNode(key, node.right, node);

		return null;
	}

	private void replaceNode(Node<K, V> parent, Node<K, V> target, Node<K, V> replacement)
	{
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

	private Node<K, V> minimum(Node<K, V> node)
	{
		while (node != null) {
			if (node.left == null)
				return node;
			node = node.left;
		}

		return null;
	}

	/**
	 * Copies all of the mappings from the specified map to this map (optional operation).  The effect of this call is
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
	 * Removes all of the mappings from this map (optional operation). The map will be empty after this call returns.
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
	 * Collection implementation allowing for collection methods on the values in the {@link HashMap}.
	 */
	private class TreeMapValueCollection implements Collection<V>
	{

		/**
		 * Returns the number of elements in this collection.
		 *
		 * @return The number of elements in this collection.
		 */
		@Override public int size()
		{
			return size;
		}

		/**
		 * Returns <code>true</code> if this collection contains no elements.
		 *
		 * @return <code>true</code> if this collection contains no elements
		 */
		@Override public boolean isEmpty()
		{
			return size == 0;
		}

		/**
		 * Returns <code>true</code> if this collection contains the specified element.
		 *
		 * @param o element whose presence in this collection is to be tested.
		 *
		 * @return <code>true</code> if this collection contains the specified element.
		 */
		@Override public boolean contains(Object o)
		{
			Node<K, V> node = searchNode(o, root);

			return node != null;
		}

		/**
		 * Returns an iterator over the elements in this collection.  There are no guarantees concerning the order in
		 * which the elements are returned.
		 *
		 * @return an <tt>Iterator</tt> over the elements in this collection.
		 */
		@Override public Iterator<V> iterator()
		{
			return new HashMapValueIterator();
		}

		/**
		 * Iterator implementation for iterating through the values in the {@link HashMap}.
		 */
		private class HashMapValueIterator extends TreeMapIterator<V>
		{

			/**
			 * Returns the next element in the iteration.
			 *
			 * @return the next element in the iteration
			 * @throws NoSuchElementException if the iteration has no more elements
			 */
			@Override public V next()
			{
				return super.nextNode().value;
			}
		}

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
		 * Unsupported operation.
		 *
		 * @throws UnsupportedOperationException
		 */
		@Override public boolean add(V v)
		{
			throw new UnsupportedOperationException();
		}

		/**
		 * Removes a single instance of the specified element from this collection, if it is present.
		 *
		 * @param o element to be removed from this collection, if present
		 *
		 * @return <tt>true</tt> if an element was removed as a result of this call
		 */
		@Override public boolean remove(Object o)
		{
			Node<K, V> removed = removeNode(searchNode(o, root));

			return removed != null;
		}

		/**
		 * Returns <tt>true</tt> if this collection contains all of the elements in the specified collection.
		 *
		 * @param c collection to be checked for containment in this collection
		 *
		 * @return <tt>true</tt> if this collection contains all of the elements in the specified collection.
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
		 * Unsupported operation.
		 *
		 * @throws UnsupportedOperationException
		 */
		@Override public boolean addAll(Collection<? extends V> c)
		{
			throw new UnsupportedOperationException();
		}

		/**
		 * Removes all of this collection's elements that are also contained in the specified collection. After this
		 * call returns, this collection will contain no elements in common with the specified collection.
		 *
		 * @param c collection containing elements to be removed from this collection.
		 *
		 * @return <tt>true</tt> if this collection changed as a result of the call.
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
		 * Retains only the elements in this collection that are contained in the specified collection.  In other
		 * words,
		 * <p>
		 * removes from this collection all of its elements that are not contained in the specified collection.
		 *
		 * @param c collection containing elements to be retained in this collection
		 *
		 * @return <tt>true</tt> if this collection changed as a result of the call
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
		 * Removes all of the elements from this collection.
		 */
		@Override public void clear()
		{
			TreeMap.this.clear();
		}
	}

	private Node<K, V> getFirst()
	{
		if (root == null)
			return null;

		Node<K, V> current = root;
		while (current.left != null)
			current = current.left;

		return current;
	}

	private final class TreeMapNodeIterator extends TreeMapIterator<Node<K, V>>
	{

		/**
		 * Returns the next element in the iteration.
		 *
		 * @return the next element in the iteration
		 * @throws NoSuchElementException if the iteration has no more elements
		 */
		@Override public Node<K, V> next()
		{
			return nextNode();
		}
	}

	abstract private class TreeMapIterator<T> implements Iterator<T>
	{

		private Node<K, V> next;
		private Node<K, V> previous;

		public TreeMapIterator()
		{
			next = getFirst();
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
		 * Removes from the underlying collection the last element returned by this iterator (optional operation). This
		 * method can be called only once per call to {@link #next}.  The behavior of an iterator is unspecified if the
		 * underlying collection is modified while the iteration is in progress in any way other than by calling this
		 * method.
		 *
		 * @throws UnsupportedOperationException if the {@code remove} operation is not supported by this iterator
		 * @throws IllegalStateException         if the {@code next} method has not yet been called, or the {@code
		 *                                       remove} method has already been called after the last call to the
		 *                                       {@code next} method
		 * @implSpec The default implementation throws an instance of {@link UnsupportedOperationException} and
		 * performs
		 * no other action.
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
	 * Returns a {@link Set} view of the keys contained in this map. The set is backed by the map, so changes to the
	 * map
	 * are reflected in the set, and vice-versa.  If the map is modified while an iteration over the set is in progress
	 * (except through the iterator's own <tt>remove</tt> operation), the results of the iteration are undefined.  The
	 * set supports element removal, which removes the corresponding mapping from the map, via the
	 * <tt>Iterator.remove</tt>, <tt>Set.remove</tt>, <tt>removeAll</tt>, <tt>retainAll</tt>, and <tt>clear</tt>
	 * operations.  It does not support the <tt>add</tt> or <tt>addAll</tt> operations.
	 *
	 * @return a set view of the keys contained in this map
	 */
	@Override public Set<K> keySet()
	{
		if (cacheKeySet == null)
			cacheKeySet = new TreeMapKeySet();

		return cacheKeySet;
	}

	private final class TreeMapKeySet implements Set<K>
	{
		/**
		 * Returns the number of elements in this set (its cardinality).  If this set contains more than
		 * <tt>Integer.MAX_VALUE</tt> elements, returns <tt>Integer.MAX_VALUE</tt>.
		 *
		 * @return the number of elements in this set (its cardinality)
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
		 * Returns <tt>true</tt> if this set contains the specified element. More formally, returns <tt>true</tt> if
		 * and
		 * only if this set contains an element <tt>e</tt> such that <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o
		 * .equals(e))</tt>.
		 *
		 * @param o element whose presence in this set is to be tested
		 *
		 * @return <tt>true</tt> if this set contains the specified element
		 * @throws ClassCastException   if the type of the specified element is incompatible with this set (<a
		 *                              href="Collection.html#optional-restrictions">optional</a>)
		 * @throws NullPointerException if the specified element is null and this set does not permit null elements (<a
		 *                              href="Collection.html#optional-restrictions">optional</a>)
		 */
		@Override public boolean contains(Object o)
		{
			return containsKey(o);
		}

		/**
		 * Returns an iterator over the elements in this set.  The elements are returned in no particular order (unless
		 * this set is an instance of some class that provides a guarantee).
		 *
		 * @return an iterator over the elements in this set
		 */
		@Override public Iterator<K> iterator()
		{
			return new TreeMapKeyIterator();
		}

		private class TreeMapKeyIterator extends TreeMapIterator<K>
		{

			/**
			 * Returns the next element in the iteration.
			 *
			 * @return the next element in the iteration
			 * @throws NoSuchElementException if the iteration has no more elements
			 */
			@Override public K next()
			{
				return nextNode().key;
			}
		}

		/**
		 * Returns an array containing all of the elements in this set. If this set makes any guarantees as to what
		 * order its elements are returned by its iterator, this method must return the elements in the same order.
		 * <p>
		 * <p>The returned array will be "safe" in that no references to it are maintained by this set.  (In other
		 * words, this method must allocate a new array even if this set is backed by an array). The caller is thus
		 * free
		 * to modify the returned array.
		 * <p>
		 * <p>This method acts as bridge between array-based and collection-based APIs.
		 *
		 * @return an array containing all the elements in this set
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
		 * Returns an array containing all of the elements in this set; the runtime type of the returned array is that
		 * of the specified array. If the set fits in the specified array, it is returned therein. Otherwise, a new
		 * array is allocated with the runtime type of the specified array and the size of this set.
		 * <p>
		 * <p>If this set fits in the specified array with room to spare (i.e., the array has more elements than this
		 * set), the element in the array immediately following the end of the set is set to <tt>null</tt>.  (This is
		 * useful in determining the length of this set <i>only</i> if the caller knows that this set does not contain
		 * any null elements.)
		 * <p>
		 * <p>If this set makes any guarantees as to what order its elements are returned by its iterator, this method
		 * must return the elements in the same order.
		 * <p>
		 * <p>Like the {@link #toArray()} method, this method acts as bridge between array-based and collection-based
		 * APIs. Further, this method allows precise control over the runtime type of the output array, and may, under
		 * certain circumstances, be used to save allocation costs.
		 * <p>
		 * <p>Suppose <tt>x</tt> is a set known to contain only strings. The following code can be used to dump the set
		 * into a newly allocated array of <tt>String</tt>:
		 * <p>
		 * <pre>
		 *     String[] y = x.toArray(new String[0]);</pre>
		 * <p>
		 * Note that <tt>toArray(new Object[0])</tt> is identical in function to <tt>toArray()</tt>.
		 *
		 * @param a the array into which the elements of this set are to be stored, if it is big enough; otherwise, a
		 *          new array of the same runtime type is allocated for this purpose.
		 *
		 * @return an array containing all the elements in this set
		 * @throws ArrayStoreException  if the runtime type of the specified array is not a supertype of the runtime
		 *                              type of every element in this set
		 * @throws NullPointerException if the specified array is null
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
				r[index++] = (T) iterator.next().key;
			}

			return r;
		}

		/**
		 * Adds the specified element to this set if it is not already present (optional operation).  More formally,
		 * adds the specified element <tt>e</tt> to this set if the set contains no element <tt>e2</tt> such that
		 * <tt>(e==null&nbsp;?&nbsp;e2==null&nbsp;:&nbsp;e.equals(e2))</tt>. If this set already contains the element,
		 * the call leaves the set unchanged and returns <tt>false</tt>.  In combination with the restriction on
		 * constructors, this ensures that sets never contain duplicate elements.
		 * <p>
		 * <p>The stipulation above does not imply that sets must accept all elements; sets may refuse to add any
		 * particular element, including <tt>null</tt>, and throw an exception, as described in the specification for
		 * {@link Collection#add Collection.add}. Individual set implementations should clearly document any
		 * restrictions on the elements that they may contain.
		 *
		 * @param k element to be added to this set
		 *
		 * @return <tt>true</tt> if this set did not already contain the specified element
		 * @throws UnsupportedOperationException if the <tt>add</tt> operation is not supported by this set
		 * @throws ClassCastException            if the class of the specified element prevents it from being added to
		 *                                       this set
		 * @throws NullPointerException          if the specified element is null and this set does not permit null
		 *                                       elements
		 * @throws IllegalArgumentException      if some property of the specified element prevents it from being added
		 *                                       to this set
		 */
		@Override public boolean add(K k)
		{
			throw new UnsupportedOperationException();
		}

		/**
		 * Removes the specified element from this set if it is present (optional operation).  More formally,
		 * removes an
		 * element <tt>e</tt> such that <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</tt>, if this set
		 * contains such an element.  Returns <tt>true</tt> if this set contained the element (or equivalently, if this
		 * set changed as a result of the call).  (This set will not contain the element once the call returns.)
		 *
		 * @param o object to be removed from this set, if present
		 *
		 * @return <tt>true</tt> if this set contained the specified element
		 * @throws ClassCastException            if the type of the specified element is incompatible with this set (<a
		 *                                       href="Collection.html#optional-restrictions">optional</a>)
		 * @throws NullPointerException          if the specified element is null and this set does not permit null
		 *                                       elements
		 *                                       (<a href="Collection.html#optional-restrictions">optional</a>)
		 * @throws UnsupportedOperationException if the <tt>remove</tt> operation is not supported by this set
		 */
		@Override public boolean remove(Object o)
		{
			return TreeMap.this.remove(o) != null;
		}

		/**
		 * Returns <tt>true</tt> if this set contains all of the elements of the specified collection.  If the
		 * specified
		 * collection is also a set, this method returns <tt>true</tt> if it is a <i>subset</i> of this set.
		 *
		 * @param c collection to be checked for containment in this set
		 *
		 * @return <tt>true</tt> if this set contains all of the elements of the specified collection
		 * @throws ClassCastException   if the types of one or more elements in the specified collection are
		 *                              incompatible with this set
		 *                              (<a href="Collection.html#optional-restrictions">optional</a>)
		 * @throws NullPointerException if the specified collection contains one or more null elements and this set
		 * does
		 *                              not permit null elements
		 *                              (<a href="Collection.html#optional-restrictions">optional</a>),
		 *                              or if the specified collection is null
		 * @see #contains(Object)
		 */
		@Override public boolean containsAll(Collection<?> c)
		{
			if (c == null || c.isEmpty())
				return true;

			TreeMapNodeIterator iterator = new TreeMapNodeIterator();
			while (iterator.hasNext())
				if (!c.contains(iterator.next().key))
					return false;

			return true;
		}

		/**
		 * Adds all of the elements in the specified collection to this set if they're not already present (optional
		 * operation).  If the specified collection is also a set, the <tt>addAll</tt> operation effectively modifies
		 * this set so that its value is the <i>union</i> of the two sets.  The behavior of this operation is undefined
		 * if the specified collection is modified while the operation is in progress.
		 *
		 * @param c collection containing elements to be added to this set
		 *
		 * @return <tt>true</tt> if this set changed as a result of the call
		 * @throws UnsupportedOperationException if the <tt>addAll</tt> operation is not supported by this set
		 * @throws ClassCastException            if the class of an element of the specified collection prevents it
		 * from
		 *                                       being added to this set
		 * @throws NullPointerException          if the specified collection contains one or more null elements and
		 * this
		 *                                       set does not permit null elements, or if the specified collection is
		 *                                       null
		 * @throws IllegalArgumentException      if some property of an element of the specified collection prevents it
		 *                                       from being added to this set
		 * @see #add(Object)
		 */
		@Override public boolean addAll(Collection<? extends K> c)
		{
			throw new UnsupportedOperationException();
		}

		/**
		 * Retains only the elements in this set that are contained in the specified collection (optional operation) .
		 * In other words, removes from this set all of its elements that are not contained in the specified collection
		 * . If the specified collection is also a set, this operation effectively modifies this set so that its value
		 * is the <i>intersection</i> of the two sets.
		 *
		 * @param c collection containing elements to be retained in this set
		 *
		 * @return <tt>true</tt> if this set changed as a result of the call
		 * @throws UnsupportedOperationException if the <tt>retainAll</tt> operation is not supported by this set
		 * @throws ClassCastException            if the class of an element of this set is incompatible with the
		 *                                       specified collection
		 *                                       (<a href="Collection.html#optional-restrictions">optional
		 *                                       </a>)
		 * @throws NullPointerException          if this set contains a null element and the specified collection does
		 *                                       not permit null elements
		 *                                       (<a href="Collection.html#optional-restrictions">optional</a>),
		 *                                       or if the specified collection is null
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
		 * Removes from this set all of its elements that are contained in the specified collection (optional
		 * operation). If the specified collection is also a set, this operation effectively modifies this set so that
		 * its value is the <i>asymmetric set difference</i> of the two sets.
		 *
		 * @param c collection containing elements to be removed from this set
		 *
		 * @return <tt>true</tt> if this set changed as a result of the call
		 * @throws UnsupportedOperationException if the <tt>removeAll</tt> operation is not supported by this set
		 * @throws ClassCastException            if the class of an element of this set is incompatible with the
		 *                                       specified collection
		 *                                       (<a href="Collection.html#optional-restrictions">optional</a>)
		 * @throws NullPointerException          if this set contains a null element and the specified collection does
		 *                                       not permit null elements
		 *                                       (<a href="Collection.html#optional-restrictions">optional</a>),
		 *                                       or if the specified collection is null
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
		 * Removes all of the elements from this set (optional operation). The set will be empty after this call
		 * returns.
		 *
		 * @throws UnsupportedOperationException if the <tt>clear</tt> method is not supported by this set
		 */
		@Override public void clear()
		{
			TreeMap.this.clear();
		}
	}

	/**
	 * Returns a {@link Set} view of the mappings contained in this map. The set is backed by the map, so changes to
	 * the
	 * map are reflected in the set, and vice-versa.  If the map is modified while an iteration over the set is in
	 * progress (except through the iterator's own <tt>remove</tt> operation, or through the <tt>setValue</tt>
	 * operation
	 * on a map entry returned by the iterator) the results of the iteration are undefined.  The set supports element
	 * removal, which removes the corresponding mapping from the map, via the <tt>Iterator.remove</tt>,
	 * <tt>Set.remove</tt>, <tt>removeAll</tt>, <tt>retainAll</tt> and <tt>clear</tt> operations.  It does not support
	 * the <tt>add</tt> or <tt>addAll</tt> operations.
	 *
	 * @return a set view of the mappings contained in this map
	 */
	@Override public Set<Entry<K, V>> entrySet()
	{
		return null;
	}

	private V putNode(K key, V value)
	{
		if (root == null) {
			root = new Node<>(key, value);
			size++;
			return null;
		}

		return putNode(key, value, root);
	}

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

	private Node<K, V> getNode(K key, V value, Node<K, V> node)
	{
		if (node == null)
			return null;

		if ((key == null ? key == node.key : key.equals(node.key)) &&
			(value == null ? node.value == null : value.equals(node.value))) {
			return node;
		}

		int compare = comparator.compare(key, node.key);

		if (compare < 0)
			return getNode(key, node.left);
		if (compare > 0)
			return getNode(key, node.right);

		return null;
	}

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
