/*
 * main.h
 *
 *  Created on: Aug 8, 2016
 *      Author: KimNam
 */

#ifndef __sleep_assistant_service_H__
#define __sleep_assistant_service_H__

#include <tizen.h>
#include <service_app.h>
#include <dlog.h>
#include <sap.h>
#include <Elementary.h>
#include <system_settings.h>
#include <efl_extension.h>
#include <dlog.h>
#include <sensor.h>
#include <glib.h>

typedef struct appdata
{
	sensor_h sensor; // Sensor handle
	sensor_listener_h listener; // Sensor listener handle
} appdata_s;

typedef struct _sensor_info
{
	sensor_h sensor; /**< Sensor handle */
	sensor_listener_h sensor_listener;
} sensorinfo;

static sensorinfo HRM_info;

#define TAG "Sleep Assistant Service"
#define GEAR_APP_ID "org.tizen.sleep_assistant_gear"
#define STRNCMP_LIMIT 256

void start_heartrate_sensor(appdata_s *ad);
void stop_heartrate_sensor(appdata_s *ad);
void initialize_sap();
void update_ui(char *data);
gboolean send_data(char *message);

#ifdef  LOG_TAG
#undef  LOG_TAG
#endif
#define LOG_TAG "sleep_assistant_service"


#endif /* __sleep_assistant_service_H__ */
