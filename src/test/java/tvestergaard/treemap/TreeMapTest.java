package tvestergaard.treemap;

import com.nitorcreations.junit.runners.NestedRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.*;
import java.util.Map.Entry;

import static org.junit.Assert.*;

@RunWith(NestedRunner.class)
public class TreeMapTest
{

	private TreeMap<Integer, Integer> map;
	private static IntegerComparator comparator = new IntegerComparator();

	public static class IntegerComparator implements Comparator<Integer>
	{

		@Override public int compare(Integer o1, Integer o2)
		{
			if (o1 == null && o2 == null)
				return 0;

			if (o1 == null)
				return -1;

			if (o2 == null)
				return 1;

			return Integer.compare(o1, o2);
		}
	}

	@Before
	public void setUp()
	{
		this.map = new TreeMap<>(comparator);
	}

	@Test
	public void constructorMap() throws Exception
	{
		Map<Integer, Integer> parameter = new java.util.HashMap<>();
		parameter.put(0, 0);
		parameter.put(1, 1);
		parameter.put(2, 2);

		map = new TreeMap<>(Integer::compare, parameter);
		assertTrue(map.containsKey(0));
		assertTrue(map.containsKey(1));
		assertTrue(map.containsKey(2));
	}

	@Test
	public void size() throws Exception
	{
		assertEquals(0, map.size());
		map.put(0, 0);
		assertEquals(1, map.size());
		map.put(0, 0);
		assertEquals(1, map.size());
		map.put(1, 0);
		assertEquals(2, map.size());
	}

	@Test
	public void isEmpty() throws Exception
	{
		assertTrue(map.isEmpty());
		map.put(0, 0);
		assertFalse(map.isEmpty());
		map.clear();
		assertTrue(map.isEmpty());
	}

	@Test
	public void containsKey() throws Exception
	{
		assertFalse(map.containsKey(0));
		assertFalse(map.containsKey(null));
		map.put(0, 0);
		map.put(null, 1);
		assertTrue(map.containsKey(0));
		assertTrue(map.containsKey(null));
		map.remove(0);
		map.remove(null);
		assertFalse(map.containsKey(0));
		assertFalse(map.containsKey(null));
	}

	@Test
	public void containsValue() throws Exception
	{
		assertFalse(map.containsValue(1));
		assertFalse(map.containsValue(null));
		map.put(0, 1);
		map.put(1, null);
		assertTrue(map.containsValue(1));
		assertTrue(map.containsValue(null));
		map.remove(0);
		map.remove(1);
		assertFalse(map.containsValue(1));
		assertFalse(map.containsValue(null));
	}

	@Test
	public void get() throws Exception
	{
		assertNull(map.get(0));
		assertNull(map.get(null));
		map.put(0, 15);
		map.put(null, 30);
		assertEquals(15, (long) map.get(0));
		assertEquals(30, (long) map.get(null));
	}

	@Test
	public void put() throws Exception
	{
		assertFalse(map.containsKey(0));
		assertFalse(map.containsKey(null));
		map.put(0, 1);
		map.put(null, 1);
		assertTrue(map.containsKey(0));
		assertTrue(map.containsKey(null));

		assertEquals(1, (long) map.put(0, 200));
		assertEquals(1, (long) map.put(null, 200));
		assertEquals(200, (long) map.get(0));
		assertEquals(200, (long) map.get(null));
	}

	@Test
	public void remove() throws Exception
	{
		map.put(0, 250);
		map.put(null, 32);
		assertTrue(map.containsKey(0));
		assertTrue(map.containsKey(null));
		assertEquals(250, (long) map.remove(0));
		assertEquals(32, (long) map.remove(null));
	}

	@Test
	public void putAll() throws Exception
	{
		java.util.HashMap<Integer, Integer> parameter = new java.util.HashMap<>();
		parameter.put(0, 0);
		parameter.put(1, 1);
		parameter.put(2, 2);

		map.putAll(null);
		assertEquals(0, map.size());
		map.putAll(parameter);
		assertEquals(3, map.size());
	}

	@Test
	public void clear() throws Exception
	{
		map.put(0, 0);
		map.put(1, 1);
		assertTrue(map.containsKey(0));
		assertTrue(map.containsKey(1));
		map.clear();
		assertFalse(map.containsKey(0));
		assertFalse(map.containsKey(1));
	}

	@Test
	public void values() throws Exception
	{
		map.put(0, 0);
		map.put(1, 1);
		map.put(2, 2);

		Collection<Integer> values = map.values();

		assertTrue(values.contains(0));
		assertTrue(values.contains(1));
		assertTrue(values.contains(2));
	}

	public static class TreeMapValueCollectionTest
	{

		private TreeMap<Integer, Integer> map;
		private Collection<Integer>       values;

		@Before
		public void setUp()
		{
			map = new TreeMap<Integer, Integer>(new IntegerComparator());
			values = map.values();
		}

		@Test
		public void size() throws Exception
		{
			assertEquals(0, values.size());
			map.put(0, 0);
			assertEquals(1, values.size());
			map.remove(0);
			assertEquals(0, values.size());
		}

		@Test
		public void isEmpty() throws Exception
		{
			assertTrue(values.isEmpty());
			map.put(0, 0);
			assertFalse(values.isEmpty());
			map.remove(0);
			assertTrue(values.isEmpty());
		}

		@Test
		public void contains() throws Exception
		{
			assertFalse(values.contains(5));
			map.put(0, 5);
			assertTrue(values.contains(5));
			map.remove(0);
			assertFalse(values.contains(5));
		}

		public static class TreeMapValueIteratorTest
		{

			private TreeMap<Integer, Integer> map;
			private Collection<Integer>       values;
			private Iterator<Integer>         it;

			@Before
			public void setUp()
			{
				map = new TreeMap<>(comparator);
				map.put(0, 0);
				map.put(1, 1);
				map.put(2, 2);
				values = map.values();
				it = values.iterator();
			}

			@Test
			public void hasNext() throws Exception
			{
				int counter = 0;
				while (it.hasNext() == true) {
					Integer actual = it.next();
					assertEquals(counter, (long) actual);
					counter++;
				}

				assertEquals(3, counter);
			}

			@Test
			public void next() throws Exception
			{
				int counter = 0;
				while (it.hasNext() == true) {
					Integer actual = it.next();
					assertEquals(counter, (long) actual);
					counter++;
				}

				assertEquals(3, counter);
			}

			@Test(expected = NoSuchElementException.class)
			public void nextThrowsNoSuchElementException() throws Exception
			{
				it.next();
				it.next();
				it.next();
				it.next();
			}

			@Test
			public void remove() throws Exception
			{
				int counter = 0;
				while (it.hasNext() == true) {
					it.next();
					it.remove();
					counter++;
				}

				assertEquals(3, counter);
				assertTrue(values.isEmpty());
			}

			@Test(expected = IllegalStateException.class)
			public void removeThrowsIllegalStateException() throws Exception
			{
				it.remove();
			}
		}

		@Test
		public void toArray() throws Exception
		{
			int sum = 10;

			map.put(2, 2);
			map.put(3, 3);
			map.put(0, 0);
			map.put(4, 4);
			map.put(1, 1);

			Object[] array = values.toArray();
			assertEquals(5, array.length);
			for (Object o : array) {
				Integer i = (Integer) o;
				sum -= i;
			}

			assertEquals(0, sum);
		}

		@Test
		public void toArrayArgument() throws Exception
		{
			int sum = 20;

			map.put(0, 0);
			map.put(1, 1);
			map.put(2, 2);
			map.put(3, 3);
			map.put(4, 4);

			Integer[] array = values.toArray(new Integer[0]);
			assertEquals(5, array.length);
			for (Object o : array) {
				Integer i = (Integer) o;
				sum -= i;
			}

			assertEquals(10, sum);

			array = values.toArray(new Integer[5]);
			assertEquals(5, array.length);
			for (Object o : array) {
				Integer i = (Integer) o;
				sum -= i;
			}

			assertEquals(0, sum);
		}

		@Test
		public void remove() throws Exception
		{
			map.put(0, 0);
			map.put(1, null);
			assertTrue(values.contains(0));
			assertTrue(values.contains(null));
			values.remove(0);
			values.remove(null);
			assertFalse(values.contains(0));
			assertFalse(values.contains(null));
		}

		@Test
		public void containsAll() throws Exception
		{
			assertFalse(values.containsAll(null));
			assertFalse(values.containsAll(new ArrayList<>()));

			map.put(0, 0);
			map.put(1, 1);

			Collection<Integer> c = new ArrayList<>();

			assertFalse(values.containsAll(c));

			c.add(0);
			c.add(1);

			assertTrue(values.containsAll(c));
		}

		@Test(expected = UnsupportedOperationException.class)
		public void add() throws Exception
		{
			values.add(5);
		}

		@Test(expected = UnsupportedOperationException.class)
		public void addAll() throws Exception
		{
			values.add(5);
		}

		@Test
		public void removeAll() throws Exception
		{
			assertFalse(values.removeAll(null));
			assertFalse(values.removeAll(new ArrayList<>()));

			map.put(0, 0);
			map.put(1, 1);
			map.put(2, 2);

			Collection<Integer> integers = new ArrayList<>();
			integers.add(0);

			assertTrue(values.removeAll(integers));

			integers.add(1);
			integers.add(2);

			assertTrue(values.removeAll(integers));
			assertFalse(values.removeAll(integers));
		}

		@Test
		public void retainAll() throws Exception
		{
			assertFalse(values.retainAll(null));
			assertFalse(values.retainAll(new ArrayList<>()));

			map.put(0, 0);
			map.put(1, 1);
			map.put(2, 2);

			assertTrue(map.containsValue(0));
			assertTrue(map.containsValue(1));
			assertTrue(map.containsValue(2));

			Collection<Integer> integers = new ArrayList<>();
			integers.add(0);

			assertTrue(values.retainAll(integers));

			assertTrue(map.containsValue(0));
			assertFalse(map.containsValue(1));
			assertFalse(map.containsValue(2));
		}

		@Test
		public void clear() throws Exception
		{
			map.put(0, 0);
			map.put(1, 1);
			map.put(2, 2);

			assertFalse(values.isEmpty());

			values.clear();

			assertTrue(values.isEmpty());
		}
	}

	@Test
	public void entrySet() throws Exception
	{
		assertSame(map.entrySet(), map.entrySet());
	}

	public static class TreeMapEntrySetTest
	{

		private TreeMap<Integer, Integer>        map;
		private Set<Map.Entry<Integer, Integer>> set;

		private TreeMap.Node<Integer, Integer> pair(Integer key, Integer value)
		{
			return new TreeMap.Node<>(key, value);
		}

		@Before
		public void setUp()
		{
			map = new TreeMap<>(comparator);
			set = map.entrySet();
		}

		@Test
		public void size()
		{
			assertEquals(0, map.size());
			assertEquals(0, set.size());

			set.add(pair(0, 10));
			map.put(1, 20);

			assertEquals(2, map.size());
			assertEquals(2, set.size());
		}

		@Test
		public void isEmpty()
		{
			assertTrue(set.isEmpty());
			assertTrue(map.isEmpty());

			set.add(pair(1, 1));

			assertFalse(set.isEmpty());
			assertFalse(map.isEmpty());
		}

		@Test
		public void contains()
		{
			assertFalse(set.contains(pair(0, 0)));
			assertFalse(set.contains(pair(1, 1)));

			map.put(0, 0);
			set.add(pair(1, 1));

			assertFalse(set.contains(pair(0, 1)));
			assertFalse(set.contains(pair(1, 0)));

			assertTrue(set.contains(pair(0, 0)));
			assertTrue(set.contains(pair(1, 1)));
		}

		public static class TreeMapEntryIteratorTest
		{

			private TreeMap<Integer, Integer>         map;
			private Set<Entry<Integer, Integer>>      set;
			private Iterator<Entry<Integer, Integer>> it;

			@Before
			public void setUp()
			{
				map = new TreeMap<>(comparator);
				map.put(0, 0);
				map.put(1, 1);
				map.put(2, 2);
				set = map.entrySet();
				it = set.iterator();
			}

			@Test
			public void hasNext() throws Exception
			{
				int counter = 0;
				while (it.hasNext() == true) {
					Integer actual = it.next().getKey();
					assertEquals(counter, (long) actual);
					counter++;
				}

				assertEquals(3, counter);
			}

			@Test
			public void next() throws Exception
			{
				int counter = 0;
				while (it.hasNext() == true) {
					Integer actual = it.next().getKey();
					assertEquals(counter, (long) actual);
					counter++;
				}

				assertEquals(3, counter);
			}

			@Test(expected = NoSuchElementException.class)
			public void nextThrowsNoSuchElementException() throws Exception
			{
				it.next();
				it.next();
				it.next();
				it.next();
			}

			@Test
			public void remove() throws Exception
			{
				int counter = 0;
				while (it.hasNext() == true) {
					it.next();
					it.remove();
					counter++;
				}

				assertEquals(3, counter);
				assertTrue(set.isEmpty());
			}

			@Test(expected = IllegalStateException.class)
			public void removeThrowsIllegalStateException() throws Exception
			{
				it.remove();
			}
		}

		@Test
		public void toArray() throws Exception
		{
			assertEquals(0, set.toArray().length);
			assertEquals(0, set.size());

			set.add(pair(0, 0));
			map.put(1, 1);

			assertEquals(2, set.toArray().length);
			assertEquals(pair(0, 0), set.toArray()[0]);
			assertEquals(pair(1, 1), set.toArray()[1]);
		}

		@Test
		public void toArrayT() throws Exception
		{
			set.toArray((TreeMap.Node<Integer, Integer>[]) new TreeMap.Node[0]);

			assertEquals(0, set.toArray((TreeMap.Node<Integer, Integer>[]) new TreeMap.Node[0]).length);
			assertEquals(0, set.size());

			set.add(pair(0, 0));
			map.put(1, 1);

			TreeMap.Node<Integer, Integer>[] array = set.toArray((TreeMap.Node<Integer, Integer>[]) new TreeMap
					.Node[0]);

			assertEquals(2, array.length);
			assertEquals(pair(0, 0), array[0]);
			assertEquals(pair(1, 1), array[1]);
		}

		@Test
		public void add() throws Exception
		{
			assertFalse(set.contains(pair(0, 0)));
			assertFalse(map.containsKey(0));
			set.add(pair(0, 0));
			assertTrue(set.contains(pair(0, 0)));
			assertTrue(map.containsKey(0));
		}

		@Test
		public void remove() throws Exception
		{
			set.add(pair(1, 1));
			set.add(pair(11, 11));

			assertTrue(set.contains(pair(1, 1)));
			assertTrue(set.contains(pair(11, 11)));
			assertTrue(map.containsKey(1));

			assertTrue(set.remove(pair(1, 1)));
			assertFalse(set.contains(pair(1, 1)));
			assertFalse(map.containsKey(1));
		}

		@Test
		public void containsAll() throws Exception
		{
			set.add(pair(0, 0));
			set.add(pair(1, 1));

			Collection<TreeMap.Node<Integer, Integer>> c;

			c = new ArrayList<>();
			assertFalse(set.containsAll(c));

			c = new ArrayList<>();
			c.add(pair(0, 0));
			assertFalse(set.containsAll(c));

			c = new ArrayList<>();
			c.add(pair(0, 0));
			c.add(pair(1, 1));
			assertTrue(set.containsAll(c));
		}

		@Test
		public void addAll() throws Exception
		{
			Collection<TreeMap.Node<Integer, Integer>> c;

			c = new ArrayList<>();

			c.add(pair(0, 0));
			assertTrue(set.addAll(c));
			assertFalse(set.addAll(c));
			assertTrue(set.contains(pair(0, 0)));
			assertTrue(map.containsKey(0));

			c.add(pair(1, 1));
			assertTrue(set.addAll(c));
			assertEquals(2, set.size());
			assertFalse(set.addAll(c));
			assertEquals(2, set.size());
		}

		@Test
		public void retainAll() throws Exception
		{
			set.add(pair(0, 0));
			set.add(pair(1, 1));
			set.add(pair(2, 2));

			Collection<TreeMap.Node<Integer, Integer>> c;

			c = new ArrayList<>();
			assertTrue(set.retainAll(c));
			assertTrue(set.isEmpty());

			set.add(pair(0, 0));
			set.add(pair(1, 1));
			set.add(pair(2, 2));

			assertEquals(3, set.size());

			c.add(pair(0, 0));
			c.add(pair(1, 1));

			set.retainAll(c);
			assertEquals(2, set.size());
			assertEquals(2, map.size());
			assertTrue(set.contains(pair(0, 0)));
			assertTrue(set.contains(pair(1, 1)));
			assertFalse(set.contains(pair(2, 2)));
		}

		@Test
		public void removeAll()
		{
			set.add(pair(0, 0));
			set.add(pair(1, 1));
			set.add(pair(2, 2));

			Collection<TreeMap.Node<Integer, Integer>> c;

			c = new ArrayList<>();
			assertFalse(set.removeAll(c));
			assertEquals(3, set.size());

			c = new ArrayList<>();
			c.add(pair(1, 1));
			c.add(pair(2, 2));

			assertTrue(set.removeAll(c));
			assertTrue(set.contains(pair(0, 0)));
			assertFalse(set.contains(pair(1, 1)));
			assertFalse(set.contains(pair(2, 2)));
		}

		@Test
		public void clear() throws Exception
		{
			set.add(pair(0, 0));
			assertFalse(set.isEmpty());
			set.clear();
			assertTrue(set.isEmpty());
		}
	}

	@Test
	public void keySet() throws Exception
	{
		assertSame(map.keySet(), map.keySet());
	}

	public static class TreeMapKeySetTest
	{

		private TreeMap<Integer, Integer> map;
		private Set<Integer>              set;

		@Before
		public void setUp()
		{
			map = new TreeMap<>(comparator);
			set = map.keySet();
		}

		@Test
		public void size()
		{
			assertEquals(0, map.size());
			assertEquals(0, set.size());
			map.put(1, 20);
			assertEquals(1, map.size());
			assertEquals(1, set.size());
		}

		@Test
		public void isEmpty()
		{
			assertTrue(set.isEmpty());
			assertTrue(map.isEmpty());

			map.put(0, 1);

			assertFalse(set.isEmpty());
			assertFalse(map.isEmpty());
		}

		@Test
		public void contains() throws Exception
		{
			map.put(0, 0);
			assertTrue(set.contains(0));
			assertFalse(set.contains(1));
		}

		public static class TreeMapKeyIteratorTest
		{

			private TreeMap<Integer, Integer> map;
			private Set<Integer>              set;
			private Iterator<Integer>         it;

			@Before
			public void setUp()
			{
				map = new TreeMap<>(comparator);
				map.put(0, 0);
				map.put(1, 1);
				map.put(2, 2);
				set = map.keySet();
				it = set.iterator();
			}

			@Test
			public void hasNext() throws Exception
			{
				int counter = 0;
				while (it.hasNext() == true) {
					Integer actual = it.next();
					assertEquals(counter, (long) actual);
					counter++;
				}

				assertEquals(3, counter);
			}

			@Test
			public void next() throws Exception
			{
				int counter = 0;
				while (it.hasNext() == true) {
					Integer actual = it.next();
					assertEquals(counter, (long) actual);
					counter++;
				}

				assertEquals(3, counter);
			}

			@Test(expected = NoSuchElementException.class)
			public void nextThrowsNoSuchElementException() throws Exception
			{
				it.next();
				it.next();
				it.next();
				it.next();
			}

			@Test
			public void remove() throws Exception
			{
				int counter = 0;
				while (it.hasNext() == true) {
					it.next();
					it.remove();
					counter++;
				}

				assertEquals(3, counter);
				assertTrue(set.isEmpty());
			}

			@Test(expected = IllegalStateException.class)
			public void removeThrowsIllegalStateException() throws Exception
			{
				it.remove();
			}
		}

		@Test
		public void toArray() throws Exception
		{
			assertEquals(0, set.toArray().length);

			map.put(0, 0);
			map.put(1, 1);
			map.put(2, 2);

			Object[] array = set.toArray();
			assertEquals(0, array[0]);
			assertEquals(1, array[1]);
			assertEquals(2, array[2]);
			assertEquals(3, array.length);
		}

		@Test
		public void toArrayT() throws Exception
		{
			assertEquals(0, set.toArray(new Long[0]).length);
			assertEquals(0, set.size());

			map.put(0, 0);
			map.put(1, 1);

			Integer[] array = set.toArray(new Integer[2]);

			assertEquals(2, array.length);
			assertEquals((Object) 0, array[0]);
			assertEquals((Object) 1, array[1]);
		}

		@Test
		public void remove() throws Exception
		{
			assertFalse(set.remove(0));
			map.put(0, 0);
			assertTrue(set.contains(0));
			assertTrue(set.remove(0));
			assertFalse(set.contains(0));
		}

		@Test
		public void containsAll() throws Exception
		{
			assertTrue(set.containsAll(null));

			map.put(0, 0);
			map.put(1, 1);
			map.put(2, 2);

			Collection<Integer> c;

			c = new ArrayList<>();
			assertTrue(set.containsAll(c));

			c.add(0);
			c.add(1);
			assertFalse(set.containsAll(c));

			c.add(2);
			assertTrue(set.containsAll(c));
		}

		@Test
		public void retainAll() throws Exception
		{
			map.put(0, 0);
			assertFalse(set.isEmpty());
			assertTrue(set.retainAll(null));
			assertTrue(set.isEmpty());

			map.put(0, 0);
			map.put(1, 1);
			map.put(2, 2);

			Collection<Integer> c;

			c = new ArrayList<>();
			c.add(0);
			c.add(1);

			assertTrue(set.retainAll(c));
			assertEquals(2, set.size());
			assertTrue(set.contains(0));
			assertTrue(set.contains(1));
			assertFalse(set.contains(2));
		}

		@Test
		public void removeAll() throws Exception
		{
			map.put(0, 0);
			map.put(1, 1);
			map.put(2, 2);

			assertFalse(set.removeAll(null));
			assertEquals(3, set.size());
			Collection<Integer> c = new ArrayList<>();

			c.add(0);
			c.add(1);

			assertTrue(set.removeAll(c));

			assertEquals(1, set.size());
			assertTrue(set.contains(2));
		}

		@Test
		public void clear() throws Exception
		{
			map.put(0, 0);
			map.put(1, 1);

			assertFalse(map.isEmpty());
			assertFalse(set.isEmpty());
			assertEquals(2, set.size());

			set.clear();

			assertTrue(map.isEmpty());
			assertTrue(set.isEmpty());
			assertEquals(0, map.size());
			assertEquals(0, set.size());
		}
	}
}