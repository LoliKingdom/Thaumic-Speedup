package zone.rong.thaumicspeedup;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ConcurrentHashMapTypedMap<K, V> extends HashMap<K, V> implements ConcurrentMap<K, V> {

	public final ConcurrentHashMap<K, V> backingMap;

	public ConcurrentHashMapTypedMap(ConcurrentHashMap<K, V> backingMap) {
		this.backingMap = backingMap;
	}

	public ConcurrentHashMapTypedMap() {
		this.backingMap = new ConcurrentHashMap<>();
	}

	@Override
	public int size() {
		return backingMap.size();
	}

	@Override
	public boolean isEmpty() {
		return backingMap.isEmpty();
	}

	@Override
	public V get(Object key) {
		return backingMap.get(key);
	}

	@Override
	public boolean containsKey(Object key) {
		return backingMap.containsKey(key);
	}

	@Override
	public V put(K key, V value) {
		return backingMap.put(key, value);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		backingMap.putAll(m);
	}

	@Override
	public V remove(Object key) {
		return backingMap.remove(key);
	}

	@Override
	public void clear() {
		backingMap.clear();
	}

	@Override
	public boolean containsValue(Object value) {
		return backingMap.containsValue(value);
	}

	@Override
	public Set<K> keySet() {
		return backingMap.keySet();
	}

	@Override
	public Collection<V> values() {
		return backingMap.values();
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return backingMap.entrySet();
	}

	@Override
	public V getOrDefault(Object key, V defaultValue) {
		return backingMap.getOrDefault(key, defaultValue);
	}

	@Override
	public V putIfAbsent(K key, V value) {
		return backingMap.putIfAbsent(key, value);
	}

	@Override
	public boolean remove(Object key, Object value) {
		return backingMap.remove(key, value);
	}

	@Override
	public boolean replace(K key, V oldValue, V newValue) {
		return backingMap.replace(key, oldValue, newValue);
	}

	@Override
	public V replace(K key, V value) {
		return backingMap.replace(key, value);
	}

	@Override
	public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
		return backingMap.computeIfAbsent(key, mappingFunction);
	}

	@Override
	public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
		return backingMap.computeIfPresent(key, remappingFunction);
	}

	@Override
	public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
		return backingMap.compute(key, remappingFunction);
	}

	@Override
	public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
		return backingMap.merge(key, value, remappingFunction);
	}

	@Override
	public void forEach(BiConsumer<? super K, ? super V> action) {
		backingMap.forEach(action);
	}

	@Override
	public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
		backingMap.replaceAll(function);
	}

	@Override
	public Object clone() {
		try {
			return AbstractMap.class.getDeclaredMethod("clone").invoke(backingMap);
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
		return null;
	}

}
