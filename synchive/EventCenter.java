package synchive;

import java.util.Enumeration;
import java.util.Hashtable;


public class EventCenter
{
    public interface EventFunction {
        public void postEvent(Object obj); // generic function for function pass-in
    }
    public enum Events { // list of possible events
        something1, // comment
        something2, // comment
        something3, // comment
    }; 

    private static EventCenter self = new EventCenter();
    private Hashtable<Events, Hashtable<Object, EventFunction>> eventList; // contains a map of events to subscriber list
    
    private EventCenter()
    {
        eventList = new Hashtable<>();
    }
    
    public static EventCenter getInstance()
    {
        return self;
    }
    
    //subscribe to event with function to call when event occurs
    public void subscribeEvent(Events e, Object id, EventFunction function)
    {
//        System.out.println("Event Subscribed: " + e + " for id: " + id);
        if(eventList.containsKey(e)) //check if event exist in list
        {
            Hashtable<Object, EventFunction> event = eventList.get(e);
            
            if(!event.containsKey(id)) //check if id exist in event
            {
                event.put(id, function);
            }
            else
            {
//                System.out.println("ID: " + id + " already exist for event: " + event);
            }
        }
        else
        {
            Hashtable<Object, EventFunction> newEvent = new Hashtable<Object, EventFunction>();
            newEvent.put(id, function);
            eventList.put(e, newEvent);
        }
    }
    
    public void unsubscribeEvent(Events e, Object id)
    {
        // check if event exist, if so get the event and remove event with id
        if(eventList.containsKey(e))
        {
            Hashtable<Object, EventFunction> event = eventList.get(e);
            event.remove(id);
        }
    }
    
    public void unsubscribeAllEvents(Object id)
    {
        // for each event, remove event with id
        Enumeration<Hashtable<Object, EventFunction>> events = eventList.elements();
        while(events.hasMoreElements())
        {
            events.nextElement().remove(id);
        }
    }

    // post event to subscribers
    public void postEvent(Events e, Object obj)
    {
        if(eventList.containsKey(e))
        {
            Hashtable<Object, EventFunction> events = eventList.get(e);
            Enumeration<EventFunction> subscriberFunctions = events.elements();
            while(subscriberFunctions.hasMoreElements())
            {
                subscriberFunctions.nextElement().postEvent(obj);
            }
        }
    }
}
