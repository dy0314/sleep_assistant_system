/*
 * main.c
 *
 *  Created on: Jul 31, 2016
 *      Author: KimNam
 */

#include "main.h"

char *main_menu_names[] = {
		"Alarm Info", "Current Status", "Test",  NULL
};

typedef struct _item_data {
	int index;
	Elm_Object_Item *item;
} item_data;

static void _accerleration_value(sensor_h sensor, sensor_event_s *sensor_data, void
		*user_data)
{
	if( sensor_data->value_count < 3 )
		return;
	char buf[PATH_MAX];
	appdata_s *ad = (appdata_s*)user_data;
	sprintf(buf, "<align=center> <font_size=20> X : %0.1f / Y : %0.1f / Z : %0.1f </font> </align>",
			sensor_data->values[0], sensor_data->values[1], sensor_data->values[2]);
	//elm_object_text_set(ad->accerlerateLabel, buf); - print to screen
}

static void _HRM_value(sensor_h sensor, sensor_event_s *sensor_data, void
		*user_data)
{
	char buf[PATH_MAX];
	char buf2[PATH_MAX];
	appdata_s *ad = (appdata_s*)user_data;
	sprintf(buf, "<align=center> <font_size=20> Heart Rate : %0.1f </font> </align>", sensor_data->values[0]);
	//sprintf(buf2, "<align=center> <font_size=20> Peek-to-Peek : %0.1f </font> </align>", sensor_data->values[2]);
	//elm_object_text_set(ad->HRMLabel, buf); - print to screen
	//elm_object_text_set(ad->peekLabel, buf2);
}

static void
start_acceleration_sensor(appdata_s *ad)
{
	sensor_error_e err = SENSOR_ERROR_NONE;
	sensor_get_default_sensor(SENSOR_ACCELEROMETER, &accerlerate_info.sensor);
	err = sensor_create_listener(accerlerate_info.sensor, &accerlerate_info.sensor_listener);
	sensor_listener_set_event_cb(accerlerate_info.sensor_listener, 100, _accerleration_value, ad);
	sensor_listener_start(accerlerate_info.sensor_listener);
}

static void
start_heartrate_sensor(appdata_s *ad)
{
	sensor_error_e err = SENSOR_ERROR_NONE;
	sensor_get_default_sensor(SENSOR_HRM, &HRM_info.sensor);
	err = sensor_create_listener(HRM_info.sensor, &HRM_info.sensor_listener);
	sensor_listener_set_event_cb(HRM_info.sensor_listener, 100, _HRM_value, ad);
	sensor_listener_start(HRM_info.sensor_listener);
}

static void
win_delete_request_cb(void *data, Evas_Object *obj, void *event_info)
{
	/* To make your application go to background,
    Call the elm_win_lower() instead
    Evas_Object *win = (Evas_Object *) data;
    elm_win_lower(win); */
	ui_app_exit();
}

static void
gl_selected_cb(void *data, Evas_Object *obj, void *event_info)
{
	Elm_Object_Item *it = (Elm_Object_Item *)event_info;
	elm_genlist_item_selected_set(it, EINA_FALSE);
}

static char *
_gl_menu_title_text_get(void *data, Evas_Object *obj, const char *part)
{
	char buf[1024];

	snprintf(buf, 1023, "%s", "<align=center> <font_size=20>Sleep Assistant </font> </align>");
	return strdup(buf);
}

static char *
_gl_menu_text_get(void *data, Evas_Object *obj, const char *part)
{
	char buf[1024];
	item_data *id = (item_data *)data;
	int index = id->index;

	if (!strcmp(part, "elm.text")) {
		snprintf(buf, 1023, "%s", main_menu_names[index]);
		return strdup(buf);
	}
	return NULL;
}

static void
_gl_menu_del(void *data, Evas_Object *obj)
{
	// FIXME: Unrealized callback can be called after this.
	// Accessing Item_Data can be dangerous on unrealized callback.
	item_data *id = (item_data *)data;
	if (id) free(id);
}

static Eina_Bool
naviframe_pop_cb(void *data, Elm_Object_Item *it)
{
	ui_app_exit();
	return EINA_FALSE;
}

static void
create_list_view(appdata_s *ad)
{
	Evas_Object *genlist;
	Evas_Object *circle_genlist;
	Evas_Object *btn;
	Evas_Object *nf = ad->nf;
	Elm_Object_Item *nf_it;
	Elm_Genlist_Item_Class *itc = elm_genlist_item_class_new();
	Elm_Genlist_Item_Class *ttc = elm_genlist_item_class_new();
	Elm_Genlist_Item_Class *ptc = elm_genlist_item_class_new();
	item_data *id;
	int index = 0;

	/* Genlist */
	genlist = elm_genlist_add(nf);
	elm_genlist_mode_set(genlist, ELM_LIST_COMPRESS);
	evas_object_smart_callback_add(genlist, "selected", gl_selected_cb, NULL);

	circle_genlist = eext_circle_object_genlist_add(genlist, ad->circle_surface);
	eext_circle_object_genlist_scroller_policy_set(circle_genlist, ELM_SCROLLER_POLICY_OFF, ELM_SCROLLER_POLICY_AUTO);
	eext_rotary_object_event_activated_set(circle_genlist, EINA_TRUE);

	/* Genlist Title Item style */
	ttc->item_style = "title";
	ttc->func.text_get = _gl_menu_title_text_get;
	ttc->func.del = _gl_menu_del;

	/* Genlist Item style */
	itc->item_style = "default";
	itc->func.text_get = _gl_menu_text_get;
	itc->func.del = _gl_menu_del;

	/* Genlist Padding Item style */
	ptc->item_style = "padding";
	ptc->func.del = _gl_menu_del;

	/* Title Items Here */
	elm_genlist_item_append(genlist, ttc, NULL, NULL, ELM_GENLIST_ITEM_NONE, NULL, NULL);

	/* Main Menu Items Here */
	id = calloc(sizeof(item_data), 1);
	id->index = index++;
	id->item = elm_genlist_item_append(genlist, itc, id, NULL, ELM_GENLIST_ITEM_NONE, alarm_cb, ad);
	id = calloc(sizeof(item_data), 1);
	id->index = index++;
	id->item = elm_genlist_item_append(genlist, itc, id, NULL, ELM_GENLIST_ITEM_NONE, status_cb, ad);
	id = calloc(sizeof(item_data), 1);
	id->index = index++;
	id->item = elm_genlist_item_append(genlist, itc, id, NULL, ELM_GENLIST_ITEM_NONE, status_cb, ad);

	elm_genlist_item_append(genlist, ptc, NULL, NULL, ELM_GENLIST_ITEM_NONE, NULL, NULL);

	elm_genlist_item_class_free(itc);
	elm_genlist_item_class_free(ttc);
	elm_genlist_item_class_free(ptc);

	/* This button is set for devices which doesn't have H/W back key. */
	btn = elm_button_add(nf);
	elm_object_style_set(btn, "naviframe/end_btn/default");
	nf_it = elm_naviframe_item_push(nf, NULL, btn, NULL, genlist, "empty");
	elm_naviframe_item_pop_cb_set(nf_it, naviframe_pop_cb, ad->win);
}

static void
create_base_gui(appdata_s *ad)
{
	ad->win = elm_win_util_standard_add(PACKAGE, PACKAGE);
	elm_win_conformant_set(ad->win, EINA_TRUE);
	elm_win_autodel_set(ad->win, EINA_TRUE);

	if (elm_win_wm_rotation_supported_get(ad->win)) {
		int rots[4] = { 0, 90, 180, 270 };
		elm_win_wm_rotation_available_rotations_set(ad->win, (const int *)(&rots), 4);
	}

	evas_object_smart_callback_add(ad->win, "delete,request", win_delete_request_cb, NULL);

	/* Conformant */
	ad->conform = elm_conformant_add(ad->win);
	evas_object_size_hint_weight_set(ad->conform, EVAS_HINT_EXPAND, EVAS_HINT_EXPAND);
	elm_win_resize_object_add(ad->win, ad->conform);
	evas_object_show(ad->conform);

	// Eext Circle Surface Creation
	ad->circle_surface = eext_circle_surface_conformant_add(ad->conform);

	/* Indicator */
	/* elm_win_indicator_mode_set(ad->win, ELM_WIN_INDICATOR_SHOW); */

	/* Base Layout */
	ad->layout = elm_layout_add(ad->conform);
	evas_object_size_hint_weight_set(ad->layout, EVAS_HINT_EXPAND, EVAS_HINT_EXPAND);
	elm_layout_theme_set(ad->layout, "layout", "application", "default");
	evas_object_show(ad->layout);

	elm_object_content_set(ad->conform, ad->layout);

	/* Naviframe */
	ad->nf = elm_naviframe_add(ad->layout);
	create_list_view(ad);
	elm_object_part_content_set(ad->layout, "elm.swallow.content", ad->nf);
	eext_object_event_callback_add(ad->nf, EEXT_CALLBACK_BACK, eext_naviframe_back_cb, NULL);
	eext_object_event_callback_add(ad->nf, EEXT_CALLBACK_MORE, eext_naviframe_more_cb, NULL);

	/* Show window after base gui is set up */
	evas_object_show(ad->win);
	/*
  ad->win = elm_win_util_standard_add(PACKAGE, PACKAGE);
  elm_win_autodel_set(ad->win, EINA_TRUE);

  if (elm_win_wm_rotation_supported_get(ad->win)) {
    int rots[4] = { 0, 90, 180, 270 };
    elm_win_wm_rotation_available_rotations_set(ad->win, (const int *)(&rots), 4);
  }

  evas_object_smart_callback_add(ad->win, "delete,request", win_delete_request_cb, NULL);
  eext_object_event_callback_add(ad->win, EEXT_CALLBACK_BACK, win_back_cb, ad);


  ad->conform = elm_conformant_add(ad->win);
  elm_win_indicator_mode_set(ad->win, ELM_WIN_INDICATOR_SHOW);
  elm_win_indicator_opacity_set(ad->win, ELM_WIN_INDICATOR_OPAQUE);
  evas_object_size_hint_weight_set(ad->conform, EVAS_HINT_EXPAND, EVAS_HINT_EXPAND);
  elm_win_resize_object_add(ad->win, ad->conform);
  evas_object_show(ad->conform);


  char buf[PATH_MAX];
  bool is_supported = false;
  sensor_is_supported(SENSOR_HRM, &is_supported);
  sprintf(buf, "Heart Rate Monitor Sensor is %s", is_supported ? "support" : "not support");

  //elm_object_text_set(ad->HRMLabel, buf);
  //sleep(1);
  ad->HRMLabel = elm_label_add(ad->conform);
  ad->peekLabel = elm_label_add(ad->conform);
  ad->accerlerateLabel = elm_label_add(ad->conform);
  evas_object_move(ad->HRMLabel,20,20);
  evas_object_resize(ad->HRMLabel,300,30);
  evas_object_move(ad->peekLabel, 20, 50);
  evas_object_resize(ad->peekLabel, 300, 30);
  evas_object_move(ad->accerlerateLabel, 20, 80);
  evas_object_resize(ad->accerlerateLabel, 300, 30);
  evas_object_show(ad->HRMLabel);
  evas_object_show(ad->peekLabel);
  evas_object_show(ad->accerlerateLabel);

  evas_object_show(ad->win);
  start_acceleration_sensor(ad);
  start_heartrate_sensor(ad);
	 */
}

static bool
app_create(void *data)
{
	/* Hook to take necessary actions before main event loop starts
    Initialize UI resources and application's data
    If this function returns true, the main loop of application starts
    If this function returns false, the application is terminated */
	appdata_s *ad = data;

	create_base_gui(ad);

	return true;
}

static void
app_control(app_control_h app_control, void *data)
{
	/* Handle the launch request. */
}

static void
app_pause(void *data)
{
	/* Take necessary actions when application becomes invisible. */
}

static void
app_resume(void *data)
{
	/* Take necessary actions when application becomes visible. */
}

static void
app_terminate(void *data)
{
	/* Release all resources. */
}

static void
ui_app_lang_changed(app_event_info_h event_info, void *user_data)
{
	/*APP_EVENT_LANGUAGE_CHANGED*/
	char *locale = NULL;
	system_settings_get_value_string(SYSTEM_SETTINGS_KEY_LOCALE_LANGUAGE, &locale);
	elm_language_set(locale);
	free(locale);
	return;
}

static void
ui_app_orient_changed(app_event_info_h event_info, void *user_data)
{
	/*APP_EVENT_DEVICE_ORIENTATION_CHANGED*/
	return;
}

static void
ui_app_region_changed(app_event_info_h event_info, void *user_data)
{
	/*APP_EVENT_REGION_FORMAT_CHANGED*/
}

static void
ui_app_low_battery(app_event_info_h event_info, void *user_data)
{
	/*APP_EVENT_LOW_BATTERY*/
}

static void
ui_app_low_memory(app_event_info_h event_info, void *user_data)
{
	/*APP_EVENT_LOW_MEMORY*/
}

int
main(int argc, char *argv[])
{
	appdata_s ad = {0,};
	int ret = 0;

	ui_app_lifecycle_callback_s event_callback = {0,};
	app_event_handler_h handlers[5] = {NULL, };

	event_callback.create = app_create;
	event_callback.terminate = app_terminate;
	event_callback.pause = app_pause;
	event_callback.resume = app_resume;
	event_callback.app_control = app_control;

	ui_app_add_event_handler(&handlers[APP_EVENT_LOW_BATTERY], APP_EVENT_LOW_BATTERY, ui_app_low_battery, &ad);
	ui_app_add_event_handler(&handlers[APP_EVENT_LOW_MEMORY], APP_EVENT_LOW_MEMORY, ui_app_low_memory, &ad);
	ui_app_add_event_handler(&handlers[APP_EVENT_DEVICE_ORIENTATION_CHANGED], APP_EVENT_DEVICE_ORIENTATION_CHANGED, ui_app_orient_changed, &ad);
	ui_app_add_event_handler(&handlers[APP_EVENT_LANGUAGE_CHANGED], APP_EVENT_LANGUAGE_CHANGED, ui_app_lang_changed, &ad);
	ui_app_add_event_handler(&handlers[APP_EVENT_REGION_FORMAT_CHANGED], APP_EVENT_REGION_FORMAT_CHANGED, ui_app_region_changed, &ad);
	ui_app_remove_event_handler(handlers[APP_EVENT_LOW_MEMORY]);

	ret = ui_app_main(argc, argv, &event_callback, &ad);
	if (ret != APP_ERROR_NONE) {
		dlog_print(DLOG_ERROR, LOG_TAG, "app_main() is failed. err = %d", ret);
	}

	return ret;
}
