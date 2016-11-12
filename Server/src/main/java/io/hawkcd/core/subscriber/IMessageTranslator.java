package io.hawkcd.core.subscriber;

/**
 * Created by rado on 11.11.16.
 */
public interface IMessageTranslator<T,V> {
     V translate(T message);
}
