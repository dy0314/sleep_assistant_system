/*
 * main.h
 *
 *  Created on: Jul 31, 2016
 *      Author: KimNam
 */

#ifndef MAIN_H_
#define MAIN_H_

#include <app.h>
#include <Elementary.h>
#include <system_settings.h>
#include <efl_extension.h>
#include <dlog.h>
#include <sensor.h>


typedef struct _status_info {
	Evas_Object *win;
	Evas_Object *conform;
	Evas_Object *HRMLabel;
	Evas_Object *peekLabel;
	Evas_Object *accerlerateLabel;
} statusinfo;


typedef struct appdata {
	Evas_Object *win;
	Evas_Object *conform;
	Evas_Object *layout;
	Evas_Object *nf;
	Evas_Object *datetime;
	Evas_Object *popup;
	Evas_Object *button;
	Eext_Circle_Surface *circle_surface;
	struct tm saved_time;
} appdata_s;

typedef struct _sensor_info
{
	sensor_h sensor; /**< Sensor handle */
	sensor_listener_h sensor_listener;
} sensorinfo;

static sensorinfo HRM_info;
static sensorinfo accerlerate_info;

void alarm_cb(void *data, Evas_Object * obj, void *event_info);
void status_cb(void *data, Evas_Object * obj, void *event_info);

#ifdef  LOG_TAG
#undef  LOG_TAG
#endif
#define LOG_TAG "sleep_assistant_gear"

#if !defined(PACKAGE)
#define PACKAGE "org.example.sleep_assistant_gear"
#endif

#endif /* MAIN_H_ */
