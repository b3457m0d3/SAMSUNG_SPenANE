package
{
	import flash.events.EventDispatcher;
	import flash.events.StatusEvent;
	import flash.external.ExtensionContext;

	/**
	 * 三星SPenSDK扩展库
	 * @author mani
	 */	
	public class SPen extends EventDispatcher
	{
		private static var _instance:SPen;
		private static var extensionContext:ExtensionContext;

		public static function get instance():SPen
		{
			if(!_instance)
				_instance = new SPen();
			if(!extensionContext){
				extensionContext = ExtensionContext.createExtensionContext('SPen', '');
				extensionContext.addEventListener(StatusEvent.STATUS, onStatus);
			}
			return _instance;
		}

		public function init():void
		{
			extensionContext.call("init");
		}

		public function dispose():void
		{
			extensionContext.dispose();
		}

		public function initScratch(b:Boolean):void
		{
			extensionContext.call("initScratch", b);
		}

		public function hideScratch():void
		{
			extensionContext.call("hideScratch");			
		}

		public function removeScratch():void
		{
			extensionContext.call("removeScratch");	
		}

		public function showScratch():void
		{
			extensionContext.call("showScratch");
		}

		public function draw(name:String):void
		{
			extensionContext.call("draw", name);
		}

		public var callback:Function;


		protected static function onStatus(event:StatusEvent):void
		{
			var se:SPenEvent;
			switch(int(event.code))
			{
				case SPenEvent.DRAW_OK:
					se = new SPenEvent(event.code);
					se.drawOK = event.level == 'false' ? false : true;
					trace('draw ok', event.level);
					break;
				case SPenEvent.SCRATCH_OK:
					se = new SPenEvent(event.code);
					trace('scratch ok');
					break;
				default:
					se = new SPenEvent(SPenEvent.PEN_EVENT);
					try
					{
						var o:Object = JSON.parse(event.level);
						if(o){
							se.x = Number(o.x);
							se.y = Number(o.y);
							se.pressure = Number(o.pressure);
							se.action = int(event.code);
							se.onTouchPen = Boolean(o.onTouchPen);
						}
					} 
					catch(error:Error) 
					{
						trace("Not Json", error);
					}
					break;
			}
			trace('event code', event.code, event.level);
			instance.dispatchEvent(se);
		}

	}
}

