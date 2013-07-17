package
{
	import flash.events.Event;

	public class SPenEvent extends Event
	{

		public static const PEN_EVENT:String = "PenEvent";

		public static const DETACH:int = 100;
		/**
		 * SPen button down
		 */		
		public static const HOVER_BUTTON_DOWN:int = 101;
		public static const HOVER_BUTTON_UP:int = 102;
		public static const TOUCH_BUTTON_DOWN:int = 103;
		public static const TOUCH_BUTTON_UP:int = 104;
		public static const DRAW_OK:int = 105;
		public static const ON_TOUCH_PEN:int = 106;
		public static const SCRATCH_OK:int = 107;

		public static const ACTION_MASK:int = 255;

		public static const ACTION_DOWN:int = 0;

		public static const ACTION_UP:int = 1;

		public static const ACTION_MOVE:int = 2;

		public static const ACTION_CANCEL:int = 3;

		public static const ACTION_OUTSIDE:int = 4;

		public static const ACTION_POINTER_DOWN:int = 5;

		public static const ACTION_POINTER_UP:int = 6;

		public static const ACTION_HOVER_MOVE:int = 7;

		public static const ACTION_SCROLL:int = 8;

		public static const ACTION_HOVER_ENTER:int = 9;

		public static const ACTION_HOVER_EXIT:int = 10;

		public static const ACTION_POINTER_INDEX_MASK:int = 65280;

		public static const ACTION_POINTER_INDEX_SHIFT:int = 8;

		public static const ACTION_POINTER_1_DOWN:int = 5;

		public static const ACTION_POINTER_2_DOWN:int = 261;

		public static const ACTION_POINTER_3_DOWN:int = 517;

		public static const ACTION_POINTER_1_UP:int = 6;

		public static const ACTION_POINTER_2_UP:int = 262;

		public static const ACTION_POINTER_3_UP:int = 518;

		public static const ACTION_POINTER_ID_MASK:int = 65280;

		public static const ACTION_POINTER_ID_SHIFT:int = 8;

		public var x:Number;
		public var y:Number;
		public var pressure:Number;
		public var action:int;
		public var onTouchPen:Boolean;
		public var drawOK:Boolean;

		public function SPenEvent(type:String)
		{
			super(type, false, false);
		}
	}
}

