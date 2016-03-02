package com.rbsoftware.pfm.personalfinancemanager.charts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.util.SparseIntArray;

import com.rbsoftware.pfm.personalfinancemanager.FinanceDocument;
import com.rbsoftware.pfm.personalfinancemanager.FinanceDocumentModel;
import com.rbsoftware.pfm.personalfinancemanager.MainActivity;
import com.rbsoftware.pfm.personalfinancemanager.R;
import com.rbsoftware.pfm.personalfinancemanager.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by burzakovskiy on 3/2/2016.
 */
public class TrendsChartLoader extends AsyncTaskLoader<List<String[]>> {
    public static final String ACTION = "TrendsChartLoader.FORCELOAD";

    public TrendsChartLoader(Context context) {
        super(context);

    }

    @Override
    protected void onStartLoading() {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        IntentFilter intentFilter = new IntentFilter(ACTION);
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);

        forceLoad();
    }

    @Override
    public List<String[]> loadInBackground() {

        List<FinanceDocument> financeDocumentList = MainActivity.financeDocumentModel.queryDocumentsByDate(
                MainActivity.readFromSharedPreferences(getContext(), "periodTrend", "thisWeek"),
                MainActivity.getUserId(),
                FinanceDocumentModel.ORDER_ASC);
        int checkedLine = Integer.valueOf(MainActivity.readFromSharedPreferences(getContext(),
                "checkedLine", "0"));
        return getDataFromDocument(Utils.findMenuItemByPosition(checkedLine), financeDocumentList);
    }

    @Override
    public void deliverResult(List<String[]> data) {
      super.deliverResult(data);
    }

    @Override
    protected void onReset() {
        super.onReset();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(broadcastReceiver);
    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            forceLoad();
        }
    };


    /**
     * Fetches values from document fro line chart
     *
     * @param lineId  chart type
     * @param docList list of finance documents
     * @return data values and dates list of arrays
     */
    private List<String[]> getDataFromDocument(int lineId, List<FinanceDocument> docList) {
        int value;
        int i;
        List<String[]> data = new ArrayList<>();
        switch (lineId) {
            case R.id.popupBalance:
                i = 0;
                for (FinanceDocument doc : docList) {
                    value = doc.getTotalIncome() - doc.getTotalExpense();
                    if (i != 0) {
                        if (doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT).equals(data.get(i - 1)[1])) {
                            data.get(i - 1)[0] = String.valueOf(Integer.valueOf(data.get(i - 1)[0]) + value);
                        } else {

                            data.add(new String[]{
                                    Integer.toString(Integer.valueOf(data.get(i - 1)[0]) + value),
                                    doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                            });
                            i++;
                        }
                    } else {
                        data.add(new String[]{
                                Integer.toString(value),
                                doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                        });
                        i++;
                    }

                }
                Log.d("Trends", data.toString());
                return data;
            case R.id.popupTotalIncome:
                i = 0;
                for (FinanceDocument doc : docList) {
                    value = doc.getTotalIncome();
                    if (value != 0) {
                        if (i != 0) {
                            if (doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT).equals(data.get(i - 1)[1])) {
                                data.get(i - 1)[0] = String.valueOf(Integer.valueOf(data.get(i - 1)[0]) + value);
                            } else {
                                data.add(new String[]{
                                        Integer.toString(value),
                                        doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                                });
                                i++;
                            }
                        } else {
                            data.add(new String[]{
                                    Integer.toString(value),
                                    doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                            });
                            i++;
                        }
                    }
                }
                return data;
            case R.id.popupTotalExpense:
                i = 0;
                for (FinanceDocument doc : docList) {
                    value = doc.getTotalExpense();
                    if (value != 0) {
                        if (i != 0) {
                            if (doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT).equals(data.get(i - 1)[1])) {
                                data.get(i - 1)[0] = String.valueOf(Integer.valueOf(data.get(i - 1)[0]) + value);
                            } else {
                                data.add(new String[]{
                                        Integer.toString(value),
                                        doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                                });
                                i++;
                            }
                        } else {
                            data.add(new String[]{
                                    Integer.toString(value),
                                    doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                            });
                            i++;
                        }
                    }
                }
                return data;
            case R.id.popupSalary:
                i = 0;
                for (FinanceDocument doc : docList) {
                    value = doc.getSalary();
                    if (value != 0) {
                        if (i != 0) {
                            if (doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT).equals(data.get(i - 1)[1])) {
                                data.get(i - 1)[0] = String.valueOf(Integer.valueOf(data.get(i - 1)[0]) + value);
                            } else {
                                data.add(new String[]{
                                        Integer.toString(value),
                                        doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                                });
                                i++;
                            }
                        } else {
                            data.add(new String[]{
                                    Integer.toString(value),
                                    doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                            });
                            i++;
                        }
                    }
                }
                return data;
            case R.id.popupRentalIncome:
                i = 0;
                for (FinanceDocument doc : docList) {
                    value = doc.getRentalIncome();
                    if (i != 0) {
                        if (doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT).equals(data.get(i - 1)[1])) {
                            data.get(i - 1)[0] = String.valueOf(Integer.valueOf(data.get(i - 1)[0]) + value);
                        } else {
                            data.add(new String[]{
                                    Integer.toString(value),
                                    doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                            });
                            i++;
                        }
                    } else {
                        data.add(new String[]{
                                Integer.toString(value),
                                doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                        });
                        i++;
                    }
                }
                return data;
            case R.id.popupInterest:
                i = 0;
                for (FinanceDocument doc : docList) {
                    value = doc.getInterest();
                    if (value != 0) {
                        if (i != 0) {
                            if (doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT).equals(data.get(i - 1)[1])) {
                                data.get(i - 1)[0] = String.valueOf(Integer.valueOf(data.get(i - 1)[0]) + value);
                            } else {
                                data.add(new String[]{
                                        Integer.toString(value),
                                        doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                                });
                                i++;
                            }
                        } else {
                            data.add(new String[]{
                                    Integer.toString(value),
                                    doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                            });
                            i++;
                        }
                    }
                }
                return data;

            case R.id.popupGifts:
                i = 0;
                for (FinanceDocument doc : docList) {
                    value = doc.getGifts();
                    if (value != 0) {
                        if (i != 0) {
                            if (doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT).equals(data.get(i - 1)[1])) {
                                data.get(i - 1)[0] = String.valueOf(Integer.valueOf(data.get(i - 1)[0]) + value);
                            } else {
                                data.add(new String[]{
                                        Integer.toString(value),
                                        doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                                });
                                i++;
                            }
                        } else {
                            data.add(new String[]{
                                    Integer.toString(value),
                                    doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                            });
                            i++;
                        }
                    }
                }
                return data;

            case R.id.popupOtherIncome:
                i = 0;
                for (FinanceDocument doc : docList) {
                    value = doc.getOtherIncome();
                    if (value != 0) {
                        if (i != 0) {
                            if (doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT).equals(data.get(i - 1)[1])) {
                                data.get(i - 1)[0] = String.valueOf(Integer.valueOf(data.get(i - 1)[0]) + value);
                            } else {
                                data.add(new String[]{
                                        Integer.toString(value),
                                        doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                                });
                                i++;
                            }
                        } else {
                            data.add(new String[]{
                                    Integer.toString(value),
                                    doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                            });
                            i++;
                        }
                    }
                }
                return data;

            case R.id.popupTaxes:
                i = 0;
                for (FinanceDocument doc : docList) {
                    value = doc.getTaxes();
                    if (value != 0) {
                        if (i != 0) {
                            if (doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT).equals(data.get(i - 1)[1])) {
                                data.get(i - 1)[0] = String.valueOf(Integer.valueOf(data.get(i - 1)[0]) + value);
                            } else {
                                data.add(new String[]{
                                        Integer.toString(value),
                                        doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                                });
                                i++;
                            }
                        } else {
                            data.add(new String[]{
                                    Integer.toString(value),
                                    doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                            });
                            i++;
                        }
                    }
                }
                return data;

            case R.id.popupMortgage:
                i = 0;
                for (FinanceDocument doc : docList) {
                    value = doc.getMortgage();
                    if (value != 0) {
                        if (i != 0) {
                            if (doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT).equals(data.get(i - 1)[1])) {
                                data.get(i - 1)[0] = String.valueOf(Integer.valueOf(data.get(i - 1)[0]) + value);
                            } else {
                                data.add(new String[]{
                                        Integer.toString(value),
                                        doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                                });
                                i++;
                            }
                        } else {
                            data.add(new String[]{
                                    Integer.toString(value),
                                    doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                            });
                            i++;
                        }
                    }
                }
                return data;
            case R.id.popupCreditCard:
                i = 0;
                for (FinanceDocument doc : docList) {
                    value = doc.getCreditCard();
                    if (value != 0) {
                        if (i != 0) {
                            if (doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT).equals(data.get(i - 1)[1])) {
                                data.get(i - 1)[0] = String.valueOf(Integer.valueOf(data.get(i - 1)[0]) + value);
                            } else {
                                data.add(new String[]{
                                        Integer.toString(value),
                                        doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                                });
                                i++;
                            }
                        } else {
                            data.add(new String[]{
                                    Integer.toString(value),
                                    doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                            });
                            i++;
                        }
                    }
                }
                return data;
            case R.id.popupUtilities:
                i = 0;
                for (FinanceDocument doc : docList) {
                    value = doc.getUtilities();
                    if (value != 0) {
                        if (i != 0) {
                            if (doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT).equals(data.get(i - 1)[1])) {
                                data.get(i - 1)[0] = String.valueOf(Integer.valueOf(data.get(i - 1)[0]) + value);
                            } else {
                                data.add(new String[]{
                                        Integer.toString(value),
                                        doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                                });
                                i++;
                            }
                        } else {
                            data.add(new String[]{
                                    Integer.toString(value),
                                    doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                            });
                            i++;
                        }
                    }
                }
                return data;
            case R.id.popupFood:
                i = 0;
                for (FinanceDocument doc : docList) {
                    value = doc.getFood();
                    if (value != 0) {
                        if (i != 0) {
                            if (doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT).equals(data.get(i - 1)[1])) {
                                data.get(i - 1)[0] = String.valueOf(Integer.valueOf(data.get(i - 1)[0]) + value);
                            } else {
                                data.add(new String[]{
                                        Integer.toString(value),
                                        doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                                });
                                i++;
                            }
                        } else {
                            data.add(new String[]{
                                    Integer.toString(value),
                                    doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                            });
                            i++;
                        }
                    }
                }
                return data;
            case R.id.popupCarPayment:
                i = 0;
                for (FinanceDocument doc : docList) {
                    value = doc.getCarPayment();
                    if (value != 0) {
                        if (i != 0) {
                            if (doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT).equals(data.get(i - 1)[1])) {
                                data.get(i - 1)[0] = String.valueOf(Integer.valueOf(data.get(i - 1)[0]) + value);
                            } else {
                                data.add(new String[]{
                                        Integer.toString(value),
                                        doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                                });
                                i++;
                            }
                        } else {
                            data.add(new String[]{
                                    Integer.toString(value),
                                    doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                            });
                            i++;
                        }
                    }
                }
                return data;

            case R.id.popupPersonal:
                i = 0;
                for (FinanceDocument doc : docList) {
                    value = doc.getPersonal();
                    if (value != 0) {
                        if (i != 0) {
                            if (doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT).equals(data.get(i - 1)[1])) {
                                data.get(i - 1)[0] = String.valueOf(Integer.valueOf(data.get(i - 1)[0]) + value);
                            } else {
                                data.add(new String[]{
                                        Integer.toString(value),
                                        doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                                });
                                i++;
                            }
                        } else {
                            data.add(new String[]{
                                    Integer.toString(value),
                                    doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                            });
                            i++;
                        }
                    }
                }
                return data;
            case R.id.popupActivities:
                i = 0;
                for (FinanceDocument doc : docList) {
                    value = doc.getActivities();
                    if (value != 0) {
                        if (i != 0) {
                            if (doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT).equals(data.get(i - 1)[1])) {
                                data.get(i - 1)[0] = String.valueOf(Integer.valueOf(data.get(i - 1)[0]) + value);
                            } else {
                                data.add(new String[]{
                                        Integer.toString(value),
                                        doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                                });
                                i++;
                            }
                        } else {
                            data.add(new String[]{
                                    Integer.toString(value),
                                    doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                            });
                            i++;
                        }
                    }
                }
                return data;
            case R.id.popupOtherExpense:
                i = 0;
                for (FinanceDocument doc : docList) {
                    value = doc.getOtherExpenses();
                    if (value != 0) {
                        if (i != 0) {
                            if (doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT).equals(data.get(i - 1)[1])) {
                                data.get(i - 1)[0] = String.valueOf(Integer.valueOf(data.get(i - 1)[0]) + value);
                            } else {
                                data.add(new String[]{
                                        Integer.toString(value),
                                        doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                                });
                                i++;
                            }
                        } else {
                            data.add(new String[]{
                                    Integer.toString(value),
                                    doc.getNormalDate(FinanceDocument.DATE_FORMAT_SHORT)
                            });
                            i++;
                        }
                    }
                }
                return data;

        }
        return data;
    }
}
