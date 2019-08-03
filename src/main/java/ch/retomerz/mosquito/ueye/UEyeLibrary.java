/*
 * Copyright (c) 2019 Reto Merz
 */
package ch.retomerz.mosquito.ueye;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.DoubleByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public interface UEyeLibrary extends Library {

  UEyeLibrary INSTANCE = Native.loadLibrary("ueye_api_64", UEyeLibrary.class);

  int IS_SUCCESS = 0;

  // IDSEXP is_InitCamera(HIDS* phCam, HWND hWnd);
  int is_InitCamera(IntByReference phCam, Pointer hWnd);

  // IDSEXP is_ExitCamera(HIDS hCam);
  int is_ExitCamera(int hCam);

  // IDSEXP is_GetCameraList(PUEYE_CAMERA_LIST pucl);
  int is_GetCameraList(byte[] pucl);

  // IDSEXP is_AllocImageMem(HIDS hCam, INT width, INT height, INT bitspixel, char** ppcImgMem, int* pid);
  int is_AllocImageMem(int hCam, int width, int height, int bitspixel, PointerByReference ppcImgMem, IntByReference pid);

  // IDSEXP is_FreeImageMem(HIDS hCam, char* pcMem, int id);
  int is_FreeImageMem(int hCam, Pointer pcMem, int id);

  // IDSEXP is_SetImageMem(HIDS hCam, char* pcMem, int id);
  int is_SetImageMem(int hCam, Pointer pcMem, int id);

  // IDSEXP is_FreezeVideo(HIDS hCam, INT Wait);
  int is_FreezeVideo(int hCam, int Wait);

  // IDSEXP is_CopyImageMem(HIDS hCam, char* pcSource, int nID, char* pcDest);
  int is_CopyImageMem(int hCam, Pointer pcSource, int nID, Pointer pcDest);

  // IDSEXP is_SetColorMode(HIDS hCam, INT Mode);
  int is_SetColorMode(int hCam, int Mode);

  // IDSEXP is_SetFrameRate(HIDS hCam, double FPS, double* newFPS);
  int is_SetFrameRate(int hCam, double FPS, DoubleByReference newFPS);

  // IDSEXP is_Exposure(HIDS hCam, UINT nCommand, void* pParam, UINT cbSizeOfParam);
  int is_Exposure(int hCam, int nCommand, Pointer pParam, int cbSizeOfParam);

  // IDSEXP is_PixelClock(HIDS hCam, UINT nCommand, void* pParam, UINT cbSizeOfParam);
  int is_PixelClock(int hCam, int nCommand, Pointer pParam, int cbSizeOfParam);
}
