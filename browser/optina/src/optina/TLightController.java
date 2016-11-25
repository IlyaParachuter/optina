//*******************************************************************************
//*******************************************************************************
//                        Universal backlight control class
//*******************************************************************************
//*******************************************************************************
/*
Singleton. Reference with TLightController.GetInstance()
Default state is ENABLE.

Query TLightController.GetInstance(midlet).CanControl() to see wheether class is able to control backlight.
Query TLightController.GetInstance(midlet).CanControlBrightness() to see wheether class is able to control brightness.
Use TLightController.GetInstance(midlet).SetBrightness(brightness) to control backlight brightness.
brightness is 0 (minimum) to 255 (maximum);

 Controlling method is determined automatically.
*/

//#LIGHT{
package optina;

import javax.microedition.midlet.*;
import com.siemens.mp.game.Light;
import com.nokia.mid.ui.DeviceControl;
import com.motorola.multimedia.Lighting;
import com.motorola.funlight.*;
import javax.microedition.lcdui.Display;
import java.util.Timer;
import java.util.TimerTask;
import com.samsung.util.LCDLight;
import mmpp.media.BackLight;

//=============================================
// TLightController
//=============================================
public class TLightController extends TimerTask
{
 //light control method
 private static final byte LIGHT_NONE               = 0;
 private static final byte LIGHT_SIEMENS            = 1;
 private static final byte LIGHT_NOKIA              = 2;
 private static final byte LIGHT_MOTOROLA_LIGHT     = 3;
 private static final byte LIGHT_MOTOROLA_FUNLIGHT  = 4;
 private static final byte LIGHT_SAMSUNG            = 5;
 private static final byte LIGHT_LG                 = 6;
 private static final byte LIGHT_MIDP20             = 7;

 private byte method;
 private static TLightController inst = null;

 private Region r1,r2,r3;
 Timer   funLightsTimer;

//---- for timertask -----
 private static MIDlet midlet;
 private static int curBrightness;
 //------------------------

 //============================
 // void ApplyState()
 //============================
 private final void ApplyState()
 {
  switch (method)
  {
   case LIGHT_SIEMENS:
    {
     if (curBrightness>0)
      {
       Light.setLightOn();
      }
       else
      {
       Light.setLightOff();
      }
    }
   break;

   case LIGHT_NOKIA:
    {
     DeviceControl.setLights(0,curBrightness*100/255);
    }
   break;

   case LIGHT_MOTOROLA_LIGHT:
    if (curBrightness>0)
     {
      com.motorola.multimedia.Lighting.backlightOn();
     }
      else
     {
      com.motorola.multimedia.Lighting.backlightOff();
     }
   break;

   case LIGHT_MOTOROLA_FUNLIGHT:
    int c = curBrightness + (curBrightness << 8) + (curBrightness << 16);
    FunLight.getRegion(1).setColor(c);
    r1.setColor(0);
    r2.setColor(0);
    r3.setColor(0);
   break;

   case LIGHT_SAMSUNG:
    if (curBrightness>0)
     {
      com.samsung.util.LCDLight.on(0x0fffffff);  //max 60 seconds ?
     }
      else
     {
      com.samsung.util.LCDLight.off();
     }
   break;

   case LIGHT_LG:
    if (curBrightness>0)
     {
      mmpp.media.BackLight.on(0x0fffffff);
     }
      else
     {
      mmpp.media.BackLight.off();
     }
   break;
//#!MIDP10{
//#!SIEMENS{
   case LIGHT_MIDP20:
    if (curBrightness>0)
     {
      javax.microedition.lcdui.Display.getDisplay(midlet).flashBacklight(0x7fffffff);
     }
      else
     {
      javax.microedition.lcdui.Display.getDisplay(midlet).flashBacklight(0);
     }
   break;
//#SIEMENS}
//#MIDP10}
  }

 }

 //============================
 // run() (timer task)
 //============================
 public final void run()
 {
  ApplyState();
 }

 //=============================================
 // public TLightController()
 //=============================================
 private TLightController(MIDlet midlet)
 {
  curBrightness=(byte)255;
  this.midlet=midlet;

//#DEBUG{
//#DEBUG:  System.out.println("Initializing light controller");
//#DEBUG:  System.out.println("microedition.profiles = "+System.getProperty("microedition.profiles"));
//#DEBUG}

  method = LIGHT_NONE;

  try
  {
   Class.forName("com.siemens.mp.game.Light");
//#DEBUG{
//#DEBUG:   System.out.println("Using com.siemens.mp.game.Light");
//#DEBUG}
   method = LIGHT_SIEMENS;
  }
  catch (Exception e)
  {

  try
  {
   Class.forName("com.nokia.mid.ui.DeviceControl");
//#DEBUG{
//#DEBUG:   System.out.println("Using com.nokia.mid.ui.DeviceControl");
//#DEBUG}
   method = LIGHT_NOKIA;
  }
  catch (Exception e3)
  {

  try
  {
   Class.forName("com.motorola.funlight.FunLight");
//#DEBUG{
//#DEBUG:   System.out.println("Using com.motorola.multimedia.FunLight");
//#DEBUG}
   method = LIGHT_MOTOROLA_FUNLIGHT;
  }

  catch (Exception e1)
  {

  try
  {
   Class.forName("com.motorola.multimedia.Lighting");
//#DEBUG{
//#DEBUG:   System.out.println("Using com.motorola.multimedia.Lighting");
//#DEBUG}
   method = LIGHT_MOTOROLA_LIGHT;
  }
  catch (Exception e2)
  {


  try
  {
   Class.forName("com.samsung.util.LCDLight");

   if (LCDLight.isSupported()==false)
    {

//#DEBUG{
//#DEBUG:     System.out.println("LCDLight present, but not supported");
//#DEBUG}
     throw new Exception();
    }
//#DEBUG{
//#DEBUG:   System.out.println("Using com.samsung.LCDLight");
//#DEBUG}

   method = LIGHT_SAMSUNG;
  }
  catch (Exception e4)
  {

  try
  {
   Class.forName("mmpp.media.BackLight");

//#DEBUG{
//#DEBUG:   System.out.println("mmpp.media.BackLight");
//#DEBUG}

   method = LIGHT_LG;
  }
  catch (Exception e5)
  {

//#!MIDP10{
//#!SIEMENS{

   if (System.getProperty("microedition.profiles").indexOf("2.0")>0)
    {
//#DEBUG{
//#DEBUG:     System.out.println("javax.microedition.lcdui.Display");
//#DEBUG}
     method = LIGHT_MIDP20;
    }

//#SIEMENS}
//#MIDP10}

  }
  }
  }
  }
  }
  }

  String s = null;//hxshared.GetConfigProperty("LIGHTCONTROL");

  if (s!=null)
   {
     method = Byte.parseByte(s);
//#DEBUG{
//#DEBUG:     System.out.println("Forced light control methd:"+method);
//#DEBUG}
   }

  if (method == LIGHT_MOTOROLA_FUNLIGHT)
   {
    FunLight.getControl();
    r1 = FunLight.getRegion(2);
    r2 = FunLight.getRegion(3);
    r3 = FunLight.getRegion(4);

    funLightsTimer = new Timer();
    funLightsTimer.scheduleAtFixedRate(this,0,100);
   }
    else
   {
    funLightsTimer = new Timer();
    funLightsTimer.scheduleAtFixedRate(this,0,3000);
   }
  ApplyState();
 }

 //=============================================
 //GetInstance()
 //=============================================
 public static TLightController GetInstance(MIDlet midlet)
 {
  if (inst==null) inst = new TLightController(midlet);
  return inst;
 }

 //=============================================
 //public boolean CanControl()
 //=============================================
 public boolean CanControl()
 {
  return method!=LIGHT_NONE;
 }

 //=============================================
 //public boolean CanControlBrightness()
 //=============================================
 public boolean CanControlBrightness()
 {
  return (method==LIGHT_NOKIA) || (method==LIGHT_MOTOROLA_FUNLIGHT);
 }

 //=============================================
 //public void SetBrightness()
 //=============================================
 public void SetBrightness(int brightness)
 {
  if (curBrightness == brightness) return;
  curBrightness = brightness;
  ApplyState();
 }
}

//#LIGHT}

//*******************************************************************************
//*******************************************************************************
//                                     No light contorol
//*******************************************************************************
//*******************************************************************************

//#!LIGHT{
//#!NOKIA{
//#!MOTO{
//#LIGHT:import javax.microedition.midlet.*;
//#LIGHT:
//#LIGHT://=============================================
//#LIGHT:// TLightController
//#LIGHT://=============================================
//#LIGHT:public class TLightController
//#LIGHT:{
//#LIGHT: private static TLightController inst = null;
//#LIGHT:
//#LIGHT: public static TLightController GetInstance(MIDlet midlet)
//#LIGHT: {
//#LIGHT:  if (inst==null) inst = new TLightController();
//#LIGHT:  return inst;
//#LIGHT: }
//#LIGHT:
//#LIGHT: //=============================================
//#LIGHT: //public boolean canControl()
//#LIGHT: //=============================================
//#LIGHT: public boolean CanControl()
//#LIGHT: {
//#LIGHT:   return false;
//#LIGHT: }
//#LIGHT:
//#LIGHT: //=============================================
//#LIGHT: //public boolean canControlBrightness()
//#LIGHT: //=============================================
//#LIGHT: public boolean CanControlBrightness()
//#LIGHT: {
//#LIGHT:   return false;
//#LIGHT: }
//#LIGHT:
//#LIGHT: //=============================================
//#LIGHT: //public void SetBrightness()
//#LIGHT: //=============================================
//#LIGHT: public void SetBrightness(int brightness)
//#LIGHT: {
//#LIGHT: }
//#LIGHT:
//#LIGHT:}
//#MOTO}
//#NOKIA}
//#LIGHT}

//*******************************************************************************
//*******************************************************************************
//                                     Nokia
//*******************************************************************************
//*******************************************************************************

//#NOKIA{
//#NOKIA:import com.nokia.mid.ui.DeviceControl;
//#NOKIA:import javax.microedition.midlet.*;
//#NOKIA:
//#NOKIA://=============================================
//#NOKIA:// TLightController
//#NOKIA://=============================================
//#NOKIA:public class TLightController
//#NOKIA:{
//#NOKIA: private static TLightController inst = null;
//#NOKIA:
//#NOKIA: public static TLightController GetInstance(MIDlet midlet)
//#NOKIA: {
//#NOKIA:  if (inst==null) inst = new TLightController();
//#NOKIA:  return inst;
//#NOKIA: }
//#NOKIA:
//#NOKIA: //=============================================
//#NOKIA: //public boolean CanControl()
//#NOKIA: //=============================================
//#NOKIA: public final boolean CanControl()
//#NOKIA: {
//#NOKIA:   return true;
//#NOKIA: }
//#NOKIA:
//#NOKIA: //=============================================
//#NOKIA: //public boolean CanControlBrightness()
//#NOKIA: //=============================================
//#NOKIA: public final boolean CanControlBrightness()
//#NOKIA: {
//#NOKIA:   return true;
//#NOKIA: }
//#NOKIA:
//#NOKIA: //=============================================
//#NOKIA: //public void SetBrightness();
//#NOKIA: //=============================================
//#NOKIA: public final void SetBrightness(int brightness_)
//#NOKIA: {
//#NOKIA:  DeviceControl.setLights(0,brightness_*100/255);
//#NOKIA: }
//#NOKIA:}
//#NOKIA:
//#NOKIA}

//*******************************************************************************
//*******************************************************************************
//                                     Moto
//*******************************************************************************
//*******************************************************************************


//#MOTO{
//#MOTO:import javax.microedition.midlet.*;
//#MOTO:import com.motorola.multimedia.Lighting;
//#MOTO:
//#MOTO://=============================================
//#MOTO:// TLightController
//#MOTO://=============================================
//#MOTO:public class TLightController
//#MOTO:{
//#MOTO: private static TLightController inst = null;
//#MOTO:
//#MOTO: //=============================================
//#MOTO: //GetInstance()
//#MOTO: //=============================================
//#MOTO: public static TLightController GetInstance(MIDlet midlet)
//#MOTO: {
//#MOTO:  if (inst==null) inst = new TLightController();
//#MOTO:  return inst;
//#MOTO: }
//#MOTO:
//#MOTO: //=============================================
//#MOTO: //public boolean CanControl()
//#MOTO: //=============================================
//#MOTO: public final boolean CanControl()
//#MOTO: {
//#MOTO:   return true;
//#MOTO: }
//#MOTO:
//#MOTO: //=============================================
//#MOTO: //public boolean CanControlBrightness()
//#MOTO: //=============================================
//#MOTO: public final boolean CanControlBrightness()
//#MOTO: {
//#MOTO:  return false;
//#MOTO: }
//#MOTO:
//#MOTO: //=============================================
//#MOTO: //public void SetBrightness()
//#MOTO: //=============================================
//#MOTO: public final void SetBrightness(int brightness)
//#MOTO: {
//#MOTO:  if (brightness>0)
//#MOTO:   {
//#MOTO:    com.motorola.multimedia.Lighting.backlightOn();
//#MOTO:   }
//#MOTO:    else
//#MOTO:   {
//#MOTO:    com.motorola.multimedia.Lighting.backlightOff();
//#MOTO:   }
//#MOTO: }
//#MOTO:}
//#MOTO:
//#MOTO}
