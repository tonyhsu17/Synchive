package synchive;

import java.util.Enumeration;
import java.util.Hashtable;


/**
 * Singleton class to handle passing events to other classes without any direct relations. 
 * Classes publish events to EventCenter and EventCenter will distribute the events to any subscribers.
 * 
 * Modeled after Notification Center on the iOS.
 * 
 * TODO generate UID instead of passing it in
 * @author Tony Hsu
 * @structure Hashtable containing events to subscribers and their function to call.
 */
public class EventCenter
{
    /**
     * Delegate methods for EventFunction
     */
    public interface EventFunction {
        /**
         * Generic function for function pass-in
         * @param obj Any data 
         */
        public void postEvent(Object obj);
    }
    
    /**
     * List of possible events to subscribe or publish to.
     */
    public static enum Events { 
        /**
         * A file is being processed
         */
        ProcessingFile,
        /**
         * An error has occurred
         */
        ErrorOccurred,
        /**
         * General status
         */
        Status, 
        /**
         * Overall status
         */
        RunningStatus,
    }; 
    /**
     * List of possible events for RunningStauts.
     */
    public static enum RunningStatusEvents {
        /**
         * Program is waiting for user to start.
         */
        Waiting,
        /**
         * Program is processing files and running.
         */
        Running,
        /**
         * Program has finished it's job.
         */
        Completed,
        /**
         * Program has received an error and has stopped.
         */
        Error,
    };

    /**
     * Singleton Initialization
     */
    private static EventCenter self = new EventCenter();
    /**
     * Contains a map of events to subscriber list
     */
    private Hashtable<Events, Hashtable<Object, EventFunction>> eventList; 
    
    /**
     * Private constructor to prevent instantiating multiple instances.
     *  Use getInstance() to get singleton.
     */
    private EventCenter()
    {
        eventList = new Hashtable<>();
    }
    
    /**
     * @return Singleton of EventCenter
     */
    public static EventCenter getInstance()
    {
        return self;
    }
    
    /**
     * Subscribe to event with function to call when event occurs
     * 
     * @param e Type of Event to subscribe to
     * @param id UniqueID of the class
     * @param function Method to call when function occurs
     */
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
    
    /**
     * Unsubscribe to a specific event
     * 
     * @param e Event to unsubscribe to
     * @param id UniqueID of the class
     */
    public void unsubscribeEvent(Events e, Object id)
    {
        // check if event exist, if so get the event and remove event with id
        if(eventList.containsKey(e))
        {
            Hashtable<Object, EventFunction> event = eventList.get(e);
            event.remove(id);
        }
    }
    
    /**
     * Unsubscribe to all events
     * 
     * @param id UniqueID of the class
     */
    public void unsubscribeAllEvents(Object id)
    {
        // for each event, remove event with id
        Enumeration<Hashtable<Object, EventFunction>> events = eventList.elements();
        while(events.hasMoreElements())
        {
            events.nextElement().remove(id);
        }
    }

    /**
     * Post event to subscribers
     * 
     * @param e Specific event to post
     * @param obj Data to pass
     */
    public void postEvent(Events e, Object obj)
    {
        if(e == Events.ErrorOccurred)
        {
            System.out.println(obj); // Debug use
        }
        else if(e == Events.RunningStatus)
        {
            System.out.println("Status: " + ((Object[])obj)[1]); // Debug use
        }
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
